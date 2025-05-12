## On-premise GPT2 fine-tune demo:
- GPT-2 is an open-source model, allowing developers to download, fine-tune, and train it. This demo site is hosted on an Ubuntu server. [Click here to visit the demo](http://103.144.32.3:8080/gpt/demo-gpt2-eng.html)
- `fine-tune-gpt2-eng.py`: This script fine-tunes the lightweight GPT-2 model (distilgpt2) on a custom text dataset (data.txt) using Hugging Face's Transformers on CPU. It tokenizes the data, configures training with the Trainer API, and saves the trained model and tokenizer.
- `data.txt`: Provide raw text for the GPT-2 model to learn from. Serve as the input for the tokenizer and training process.
- `run-gpt2-eng-service.py`: This script loads a fine-tuned GPT-2 model and sets up a Flask web service that responds to text prompts via HTTP.
- `test-gpt2-eng.py`: This script loads a previously fine-tuned GPT-2 model from disk and uses it to generate text based on a given prompt.
## Dell AI RAG demo:
- Use LlamaIndex to implement RAG practice. [Click here to visit the demo](http://103.144.32.3:8080/gpt/demo-rag-dell.html)
- `dell-rag.py`: This script uses LlamaIndex to index and query documents, and it sets up a Flask web service to interact with the model.
- `dell-rag/`: The directory where the documents or files you want to index are stored
