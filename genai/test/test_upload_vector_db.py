import json
import pytest
from unittest.mock import patch, MagicMock

from genai.rag.utils.upload_data import upload_data_to_qdrant  #

@pytest.fixture
def sample_json_file(tmp_path):
    # simulate JSON file
    data = [
        {
            "embedding": [0.1] * 1536,
            "content": "Sample content",
            "chunk_id": "chunk_001",
            "type": "section",
            "title": "Intro",
            "source_url": "https://example.com/doc"
        }
    ]
    file_path = tmp_path / "embedded_chunks.json"
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(data, f)
    return file_path

@patch("genai.rag.utils.upload_data.QdrantClient")
def test_upload_data_to_qdrant(mock_qdrant_client_class, sample_json_file):
    # Create a mock QdrantClient instance
    mock_client = MagicMock()
    mock_qdrant_client_class.return_value = mock_client

    # Simulate that the collection does not exist
    mock_client.collection_exists.return_value = False

    # Call the function under test
    client = upload_data_to_qdrant(str(sample_json_file))

    mock_qdrant_client_class.assert_called_once()
    mock_client.collection_exists.assert_called_once_with("workflow_docs")
    mock_client.create_collection.assert_called_once()
    mock_client.upload_points.assert_called_once()

    args, kwargs = mock_client.upload_points.call_args
    assert kwargs["collection_name"] == "workflow_docs"
    assert len(kwargs["points"]) == 1
    assert kwargs["points"][0].payload["text"] == "Sample content"

    # Check if the returned client is the mock instance
    assert client == mock_client