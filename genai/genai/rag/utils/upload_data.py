import json
import os
from dotenv import load_dotenv
from qdrant_client import QdrantClient
from qdrant_client.models import PointStruct, VectorParams, Distance

load_dotenv()


def create_qdrant_client() -> QdrantClient:
    # create a Qdrant client instance
    client = QdrantClient(
        url=os.getenv("QDRANT_URL"),
        api_key=os.getenv("QDRANT_API_KEY"),
    )
    return client


def upload_data_to_qdrant(json_path: str) -> QdrantClient:
    try:
        client = QdrantClient(
            url=os.getenv("QDRANT_URL"),
            api_key=os.getenv("QDRANT_API_KEY"),
        )
    except Exception as e:
        raise RuntimeError(f"Failed to create Qdrant client: {e}")

    collection_name = "workflow_docs"

    # Check if the collection already exists
    if not client.collection_exists(collection_name):
        client.create_collection(
            collection_name=collection_name,
            vectors_config=VectorParams(
                size=1536,
                distance=Distance.COSINE
            )
        )

    with open(json_path, "r", encoding="utf-8") as f:
        docs = json.load(f)

    points = [
        PointStruct(id=i, vector=doc["embedding"],
                    payload={
                        "text": doc["content"],
                        "chunk_id": doc["chunk_id"],
                        "type": doc["type"],
                        "title": doc["title"],
                        "source_url": doc["source_url"]
                    })
        for i, doc in enumerate(docs)
    ]

    client.upload_points(collection_name=collection_name, points=points)

    return client


if __name__ == "__main__":
    import os

    json_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "processed",
                             "embedded_chunks.json")
    if not os.path.exists(json_path):
        raise FileNotFoundError(f"JSON file not found: {json_path}")
    client = upload_data_to_qdrant(json_path)
    print(
        f"Data uploaded to Qdrant collection '{client.get_collection('workflow_docs')}' successfully.")
