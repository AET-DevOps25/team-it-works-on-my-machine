# GenAI Q&A API

A simple FastAPI-based backend for asking questions to a language model (e.g., GPT or DeepSeek), using LangChain for prompt handling and model integration.

---

## 📁 Project Structure

```
genai/
│
├── app/
│   ├── chains/               # (Optional) Chains used in LangChain
│   └── input_query/
│       ├── ask.py            # Chain setup (create_chain function)
│       ├── main.py           # FastAPI app entry point
│       └── __init__.py
│
├── .env                      # Contains API keys and base URL
├── requirements.txt          # Project dependencies
├── README.md
```

---

## ⚙️ Setup

1. **Create virtual environment (optional but recommended)**

```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

2. **Install dependencies**

```bash
pip install -r requirements.txt
```

3. **Prepare `.env` file**

Create a `.env` file in the root directory and add:

```env
OPENAI_API_KEY=your-api-key
OPENAI_API_BASE=https://api.deepseek.com/v1  # Optional, if using DeepSeek or a proxy
```

---

## 🚀 Run the Server

Make sure you're in the root of the project (`genai/`), then:

```bash
uvicorn app.main:app --reload
```

This starts the FastAPI app at: `http://localhost:8000`

Open your browser at [http://localhost:8000/docs](http://localhost:8000/docs) to test via Swagger UI.

---

## 🧪 Test the Chain Logic Directly

You can test the core chain logic without running the API, by directly modifying and running `ask.py`:

### Example

Edit `app/input_query/ask.py`:

```python
import requests

if __name__=="__main__":
    url = "http://localhost:8000/ask"
    data = {"question": "What is LangChain?"}

    response = requests.post(url, json={"question": "Waht is the Rank of TUM?"})
    print(response.json()["response"])
```

Then run:

```bash
python app/input_query/ask.py
```

This will prompt you for input and return the model's answer directly using LangChain.
