from transformers import GPT2LMHeadModel, GPT2Tokenizer, pipeline

# Load your fine-tuned model
model_path = "./wenzhong-gpt2-finetuned"

# Load tokenizer and model
tokenizer = GPT2Tokenizer.from_pretrained(model_path)
model = GPT2LMHeadModel.from_pretrained(model_path)

# Ensure proper padding setup
tokenizer.pad_token = tokenizer.eos_token
model.config.pad_token_id = model.config.eos_token_id

# Build generation pipeline
generator = pipeline(
    "text-generation",
    model=model,
    tokenizer=tokenizer,
    device=-1  # -1 = CPU
)

# 📝 Provide a question prompt
prompt = "台北有哪些著名的料理？"

# Generate continuation
results = generator(
    prompt,
    max_length=1024,
    num_return_sequences=1,
    do_sample=True,
    top_k=50,
    top_p=0.95
)

# Output the generated answer
print("📝 模型生成：")
print(results[0]["generated_text"])
