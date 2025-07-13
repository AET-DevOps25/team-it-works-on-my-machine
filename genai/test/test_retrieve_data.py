from unittest.mock import patch, MagicMock

from genai.rag.utils.retrieve_data import retrieve_text


@patch("genai.rag.utils.retrieve_data.qdrant_client")
@patch("genai.rag.utils.retrieve_data.openai_client")
def test_retrieve_text(mock_openai_client, mock_qdrant_client):
    # Mock OpenAI embedding response
    mock_embedding_response = MagicMock()
    mock_embedding_response.data = [MagicMock(embedding=[0.1] * 1536)]
    mock_openai_client.embeddings.create.return_value = mock_embedding_response

    # Mock Qdrant search response
    mock_result = MagicMock()
    mock_result.score = 0.95
    mock_result.payload = {"text": "Matched content", "chunk_id": "chunk_001"}
    mock_qdrant_client.search.return_value = [mock_result]

    # Call the actual function
    results = retrieve_text("Test input text")

    # Validate embedding call
    mock_openai_client.embeddings.create.assert_called_once_with(
        input="Test input text",
        model="text-embedding-3-small"
    )

    # Validate Qdrant search call
    mock_qdrant_client.search.assert_called_once()
    args, kwargs = mock_qdrant_client.search.call_args
    assert kwargs["collection_name"] == "workflow_docs"
    assert kwargs["query_vector"] == [0.1] * 1536
    assert kwargs["limit"] == 3

    # Validate return
    assert len(results) == 1
    assert results[0].score == 0.95
    assert results[0].payload["chunk_id"] == "chunk_001"
