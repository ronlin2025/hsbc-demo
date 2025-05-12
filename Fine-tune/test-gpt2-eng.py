from transformers import GPT2LMHeadModel, GPT2Tokenizer

# Load your fine-tuned model and tokenizer
model_name = "./gpt2-cpu-finetuned"  # Directory where the model is saved
tokenizer = GPT2Tokenizer.from_pretrained(model_name)
model = GPT2LMHeadModel.from_pretrained(model_name)

# Set the padding token to be the same as EOS token (important for GPT2 models)
tokenizer.pad_token = tokenizer.eos_token

# Prompt to test the model
prompt = "What are some famous dishes in Taipei?"

# Tokenize the input
inputs = tokenizer.encode(prompt, return_tensors="pt")

# Generate the output
output = model.generate(inputs, max_length=150, num_return_sequences=1, no_repeat_ngram_size=2, top_k=50, top_p=0.95, temperature=0.7)

# Decode the output to text
generated_text = tokenizer.decode(output[0], skip_special_tokens=True)

# Print the generated response
print(generated_text)
