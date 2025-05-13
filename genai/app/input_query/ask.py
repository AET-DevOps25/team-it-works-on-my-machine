import requests

if __name__ == "__main__":
    url = "http://localhost:8000/ask"

    response = requests.post(url, json={"question": "Waht is the Rank of TUM?"})
    print(response.json()["response"])
