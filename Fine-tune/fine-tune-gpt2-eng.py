from transformers import GPT2LMHeadModel, GPT2Tokenizer, DataCollatorForLanguageModeling, Trainer, TrainingArguments
from datasets import load_dataset

# ✅ Use a small GPT2 model for CPU training
model_name = "distilgpt2"
tokenizer = GPT2Tokenizer.from_pretrained(model_name)
model = GPT2LMHeadModel.from_pretrained(model_name)

# ✅ Set the padding token to be the same as the EOS token
tokenizer.pad_token = tokenizer.eos_token

# ✅ Load custom plain text dataset
dataset = load_dataset("text", data_files={"train": "data.txt"})

# ✅ Tokenize
def tokenize_function(example):
    return tokenizer(example["text"], truncation=True, padding="max_length", max_length=64)

tokenized_dataset = dataset.map(tokenize_function, batched=True, remove_columns=["text"])

# ✅ Data collator for causal language modeling (no MLM)
data_collator = DataCollatorForLanguageModeling(tokenizer=tokenizer, mlm=False)

# ✅ Training arguments for CPU

training_args = TrainingArguments(
    output_dir="./gpt2-cpu-finetuned",
    overwrite_output_dir=True,
    num_train_epochs=3,
    per_device_train_batch_size=1,
    save_steps=10,
    save_total_limit=2,
    logging_steps=5,
    no_cuda=True  # Use CPU
)

# ✅ Trainer setup
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_dataset["train"],
    tokenizer=tokenizer,
    data_collator=data_collator,
)

# ✅ Begin training
trainer.train()

# ✅ Save model & tokenizer
trainer.save_model("./gpt2-cpu-finetuned")
tokenizer.save_pretrained("./gpt2-cpu-finetuned")

print("✅ Training complete.")
