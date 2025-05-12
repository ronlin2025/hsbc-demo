from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from dotenv import load_dotenv
import os

load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")

# 載入文件
documents = SimpleDirectoryReader("hsbc-rag").load_data()

# 建立索引
index = VectorStoreIndex.from_documents(documents)

# 查詢
query_engine = index.as_query_engine()
response = query_engine.query("這些文件在講什麼？")
print(response)


# Run in web service (Flask)
from flask import Flask, request
from flask_cors import CORS
import json

webservice = Flask(__name__)
CORS(webservice)  # Enable CORS for all routes

@webservice.route('/')
def prompt():
    try:
        query_value = request.args.get('query', '')
        if not query_value:
            return json.dumps({"error": "No query provided"}), 400
        query_value += " . 中文回應, 至少100個字."
        response = query_engine.query(query_value)
        return json.dumps({"question": query_value, "answer": str(response)})
    except Exception as e:
        return json.dumps({"error": str(e)}), 500


if __name__ == '__main__':
    webservice.run(host='0.0.0.0', port=5016)

