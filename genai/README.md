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
pip install uv
uv sync
```

3. **Prepare `.env` file**

Create a `.env` file in the root directory and add:

```env
OPENAI_API_KEY=<your-openai-api-key>

GENAI_PORT=3001
GENAI_URL=http://localhost:${GENAI_PORT}

# Qdrant configuration for vector storage
QDRANT_API_KEY=<your-qdrant-api-key>
QDRANT_URL=<your-qdrant-url>
```

---

## 🚀 Run the Server

Make sure you're in the root of the project (`genai/`), then:

```bash
uv run app
```

This starts the FastAPI app at: `http://localhost:3001`

Open your browser at [http://localhost:3001/docs](http://localhost:8000/docs) to test via Swagger UI.
