from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient
from genai.app.main import app

client = TestClient(app)

@patch("genai.app.main.chain")
@patch("genai.app.main.retrieve_text")
def test_analyze_yamls(mock_retrieve_text, mock_chain):
    # Mock chain.invoke()
    mock_chain.invoke.side_effect = [
        MagicMock(content="This is a short summary."),  # summary
        MagicMock(content="This is a detailed analysis.")  # detailed
    ]

    # Mock Qdrant response
    mock_retrieve_text.return_value = [
        MagicMock(payload={
            "title": "CI/CD Intro",
            "text": "This explains GitHub Actions setup."
        })
    ]

    # Use a dummy YAML content for testing
    response = client.post("/analyze-yamls", json={
        "yamls": [{
            "filename": "test.yml",
            "content": "name: test\non: push\njobs:\n  build:\n    runs-on: ubuntu-latest"
        }]
    })

    assert response.status_code == 200
    json_data = response.json()
    assert "results" in json_data
    assert len(json_data["results"]) == 1

    result = json_data["results"][0]
    assert result["filename"] == "test.yml"

    # check summary
    assert "summary" in result
    assert result["summary"] == "This is a short summary."

    # check related_docs
    assert "related_docs" in result
    assert isinstance(result["related_docs"], list)
    assert len(result["related_docs"]) == 1

    # check detailed_analysis
    assert "detailed_analysis" in result
    assert result["detailed_analysis"] == "This is a detailed analysis."
