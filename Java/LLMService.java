package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.radius.CacheManager;

public class LLMService {
    private String url;
    private String apiKey;
    /*
     * Sample of all in one JSON response format:
     * {
     * "themes": [
     * {
     * "theme": "theme1",
     * "ad_description": ["ad1", "ad2", "ad3", "ad4"]
     * }
     * ]
     * }
     */

    /*
     * Sample of theme JSON response format:
     * ["theme1", "theme2", "theme3", "theme4"]"
     */

    /*
     * Sample of asset JSON response format:
     * {
     * "theme": "theme1",
     * "ad_description": ["ad1", "ad2", "ad3", "ad4"]
     * }
     */

    public static final String ALL_IN_ONE_PROMPT = "Please generate 4 Ad Theme (Character Limit: between 30 to 60 characters) in %s language, based on the following information: URL: %s. Insight: Generate diverse and appealing selling points. And please generate 4 Ad Description (Character Limit: between 63 to 90 characters) in each theme. Don't show how many characters. Use JSON response format: {\"themes\": [{\"theme\": \"theme1\", \"ad_description\": [\"ad1\", \"ad2\", \"ad3\", \"ad4\"]}]}";
    public static final String THEME_PROMPT = "Please generate 4 Ad Theme (Character Limit: between 30 to 60 characters) in %s language, based on the following information: URL: %s. Insight: Generate diverse and appealing selling points. Use JSON response format: [\"theme1\", \"theme2\", \"theme3\", \"theme4\"]";
    public static final String ASSET_PROMPT = "Please generate 4 Ad Description (Character Limit: between 63 to 90 characters) in %s language, based on the following information: URL: %s. Insight: Promote the selling point theme: %s. Use JSON response format: {\"theme\": \"themeName\", \"ad_description\": [\"ad1\", \"ad2\", \"ad3\", \"ad4\"]}";
    public static final String LANGUAGE_PROMPT = "Return the language name for the first priority language in %s.";

    public LLMService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Get the language from the URL
    private String getLanguageFromURL(boolean enableLanguageCache) {   
        if (enableLanguageCache) {
            String cachedLanguage = CacheManager.getLanguageCache(url);
            if (cachedLanguage != null) {
                System.out.println("Cached language for URL " + url + ": " + cachedLanguage);
                return cachedLanguage;
            }
        }
        String prompt = String.format(LANGUAGE_PROMPT, url);
        String language = getLLMRecommendation(prompt);
        System.out.println("Language for URL " + url + ": " + language);
        CacheManager.setLanguageCache(url, language);
        return language;
    }

    // Old asset recommendation pattern
    public String getAssetRecommendation() {
        String language = getLanguageFromURL(false);
        String prompt = String.format(ALL_IN_ONE_PROMPT, language, url);
        String result = getLLMRecommendation(prompt);
        return getLLMRecommendation(prompt);
    }

    // Current asset recommendation pattern
    public String getAssetRecommendationWithAsync(boolean enableLanguageCache, boolean enableRecommendationCache) {
        if (enableRecommendationCache) {
            String cachedRecommendation = CacheManager.getRecommendationCache(url);
            if (cachedRecommendation != null) {
                System.out.println("Cached recommendation for URL " + url + ": " + cachedRecommendation);
                return cachedRecommendation;
            }
        }
        String language = getLanguageFromURL(enableLanguageCache);
        String themePrompt = String.format(THEME_PROMPT, language, url);

        // Call getLLMRecommendation asynchronously to get themes
        CompletableFuture<String> themesFuture = CompletableFuture
                .supplyAsync(() -> getLLMRecommendation(themePrompt));

        // Wait for themes result
        String themes = themesFuture.join(); // You can handle exceptions here if necessary

        JSONArray themesArray = new JSONArray(themes);
        StringBuilder result = new StringBuilder();
        String head = "{\"themes\": [";
        result.append(head);

        CompletableFuture<Void>[] assetFutures = new CompletableFuture[themesArray.length()];

        for (int i = 0; i < themesArray.length(); i++) {
            String theme = themesArray.getString(i);

            String assetPrompt = String.format(ASSET_PROMPT, language, url, theme);

            // Store each asynchronous call in the assetFutures array
            assetFutures[i] = CompletableFuture.supplyAsync(() -> getLLMRecommendation(assetPrompt))
                    .thenAccept(assets -> {
                        synchronized (result) {
                            result.append(assets).append(",");
                        }
                    });
        }

        // Wait for all asset recommendations to complete
        CompletableFuture.allOf(assetFutures).join();

        // Remove the trailing comma, if present
        if (result.charAt(result.length() - 1) == ',') {
            result.deleteCharAt(result.length() - 1);
        }

        String tail = "]}";
        result.append(tail);
        String resultString = result.toString();

        if (enableRecommendationCache) {
            CacheManager.setRecommendationCache(url, resultString);
        }

        return resultString;
    }

    private String getLLMRecommendation(String prompt) {
        String endpoint = "https://api.openai.com/v1/chat/completions";

        // Construct the request payload
        JSONObject message = new JSONObject();
        message.put("role", "user"); // Role can be "user", "assistant", or "system"

        message.put("content", prompt);

        JSONArray messages = new JSONArray();
        messages.put(message);

        JSONObject payload = new JSONObject();
        payload.put("model", "gpt-3.5-turbo"); // Or "gpt-4"
        payload.put("messages", messages);
        // payload.put("max_tokens", 150);
        // payload.put("temperature", 0.7);

        // Create HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        try {
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Handle the response
            if (response.statusCode() == 200) {
                // Parse the response body into a JSONObject
                JSONObject jsonResponse = new JSONObject(response.body());

                // Extract and print the generated message
                JSONArray choices = jsonResponse.getJSONArray("choices");
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject messageResponse = firstChoice.getJSONObject("message");

                String result = messageResponse.getString("content");
                return result;
            } else {
                // Print error details
                System.err.println("Error: " + response.statusCode() + " - " + response.body());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
