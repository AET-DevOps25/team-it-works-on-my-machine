import os
from dotenv import load_dotenv
from qdrant_client import QdrantClient
from openai import OpenAI
from pathlib import Path

load_dotenv()

# initialize OpenAI and Qdrant clients
openai_client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
qdrant_client = QdrantClient(
    url=os.getenv("QDRANT_URL"),
    api_key=os.getenv("QDRANT_API_KEY"),
    prefer_grpc=False
)


def read_workflow(workflow_path: str) -> str:
    return Path(workflow_path).read_text(encoding="utf-8")


def embed_text(text: str, model: str = "text-embedding-3-small") -> list[float]:
    response = openai_client.embeddings.create(
        input=text,
        model=model
    )
    return response.data[0].embedding


def retrieve_from_qdrant(collection_name: str, query_vector: list[float], top_k: int = 5):
    results = qdrant_client.search(
        collection_name=collection_name,
        query_vector=query_vector,
        limit=top_k
    )
    return results


def main(workflow_path: str, collection_name: str = "workflow_docs"):
    workflow_text = read_workflow(workflow_path)

    query_vector = embed_text(workflow_text)

    results = retrieve_from_qdrant(collection_name, query_vector)

    for i, res in enumerate(results):
        print(f"\n--- Result #{i + 1} (Score: {res.score:.4f}) ---")
        print(res.payload)


if __name__ == "__main__":
    import os

    path = os.path.join(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), ".github",
        "workflows", "ci_client.yml")
    main(path)
