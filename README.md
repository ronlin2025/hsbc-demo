
## HSBC AI ads copilot demo:
- Pure Java for building ads copilot like practice. [Click here to visit the demo](http://103.144.32.3:8080/gpt/demo-ads-copilot-hsbc.html)
- `Java/LLMService`: Java core libray for LLM service

## Dell AI RAG demo:
- Use LlamaIndex to implement RAG practice. [Click here to visit the demo](http://103.144.32.3:8080/gpt/demo-rag-hsbc.html)
- `RAG/hsbc-rag.py`: This script uses LlamaIndex to index and query documents, and it sets up a Flask web service to interact with the model.
- `RAG/hsbc-rag/`: The directory where the documents or files you want to index are stored

## On-premise GPT2 fine-tune demo:
- GPT-2 is an open-source model, allowing developers to download, fine-tune, and train it. This demo site is hosted on an Ubuntu server. [Click here to visit the demo](http://103.144.32.3:8080/gpt/demo-gpt2-eng.html)
- `Fine-tune/fine-tune-gpt2-eng.py`: This script fine-tunes the lightweight GPT-2 model (distilgpt2) on a custom text dataset (data.txt) using Hugging Face's Transformers on CPU. It tokenizes the data, configures training with the Trainer API, and saves the trained model and tokenizer.
- `Fine-tune/data.txt`: Provide raw text for the GPT-2 model to learn from. Serve as the input for the tokenizer and training process.
- `Fine-tune/run-gpt2-eng-service.py`: This script loads a fine-tuned GPT-2 model and sets up a Flask web service that responds to text prompts via HTTP.
- `Fine-tune/test-gpt2-eng.py`: This script loads a previously fine-tuned GPT-2 model from disk and uses it to generate text based on a given prompt.
