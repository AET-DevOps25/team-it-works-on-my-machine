import requests

if __name__ == "__main__":
    url = "http://localhost:8001/ask"

    response = requests.post(url, json={"question": "What is the Rank of TUM?"})
    print(response.json()["response"])
