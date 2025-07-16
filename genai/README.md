# GenAI Q&A API

A simple FastAPI-based backend for asking questions to a language model (e.g., GPT or DeepSeek), using LangChain for prompt handling and model integration.

---

## 📁 Project Structure

```
genai/                            # Root directory of the GenAI project
├── genai/                        # Python source package (import path: genai.*)
│   ├── app/
│   │   ├── input_query/
│   │   │   ├── analyze_yaml.py  # YAML analysis logic
│   │   ├── main.py              # FastAPI app entrypoint
│   │   └── open_api.py          # API routes and OpenAPI integration
│   │
│   └── rag/                     # Retrieval-Augmented Generation (RAG) modules
│       ├── data/                # Processed data for RAG
│       ├── ingest/              # Data ingestion logic
│       ├── utils/               # Utility functions for RAG
│       └── __init__.py
│
├── test/                        # Unit/integration tests
├── .env                         # Runtime environment variables
├── .env.example                 # Template for .env
├── .python-version              # Python version specification
├── Dockerfile                   # Docker build configuration
├── openapi.json                 # OpenAPI spec (for Swagger or client SDKs)
├── pyproject.toml               # Project configuration & dependencies
├── pytest.ini                   # Pytest configuration
├── uv.lock                      # Lock file (e.g. from `uv` or `poetry`)
└── README.md                    # Project documentation

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
