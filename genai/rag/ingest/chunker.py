import json
import uuid
from pathlib import Path
from typing import List, Dict


def chunk_by_heading(content_blocks: List[Dict], url: str) -> List[Dict]:
    chunks = []
    current_chunk = {"text": "", "title": "", "source_url": url}
    for block in content_blocks:
        if block["type"] == "heading" and block.get("level") == 2:
            if current_chunk["text"]:
                current_chunk["chunk_id"] = str(uuid.uuid4())
                chunks.append(current_chunk)
            current_chunk = {
                "title": block["text"],
                "text": "",
                "source_url": url
            }
        elif block["type"] in ["paragraph", "code", "list"]:
            text = block.get("text") or block.get("content") or ""
            if block["type"] == "code":
                text = f"```{block.get('language', '')}\n{text}\n```"
            elif block["type"] == "list":
                text = "\n".join(f"- {item}" for item in block.get("items", []))
            current_chunk["text"] += text + "\n\n"
    if current_chunk["text"]:
        current_chunk["chunk_id"] = str(uuid.uuid4())
        chunks.append(current_chunk)
    return chunks


if __name__ == "__main__":
    import os

    input_file = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "processed",
                             "actions_docs_raw.json")
    # read JSON
    file_path = Path(input_file)
    with file_path.open(encoding="utf-8") as f:
        raw_docs = json.load(f)

    # chunk the content
    all_chunks = []
    for entry in raw_docs:
        url = entry["url"]
        content_blocks = entry["content"]
        chunks = chunk_by_heading(content_blocks, url)
        all_chunks.extend(chunks)

    # save the chunked content
    output_file = os.path.join(os.path.dirname(os.path.dirname(__file__)), "data", "processed",
                             "actions_docs_chunked.json")
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(all_chunks, f, ensure_ascii=False, indent=2)

