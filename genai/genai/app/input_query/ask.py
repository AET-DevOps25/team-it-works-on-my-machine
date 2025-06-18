import requests
from dotenv import load_dotenv
import os

load_dotenv()

def ask():
    url = os.getenv("GENAI_URL", "http://localhost:3001") + "/ask"

    response = requests.post(url, json={"question": "What is the Rank of TUM?"})
    print(response.json()["response"])

if __name__ == "__main__":
    ask()
