import json
import re
from pathlib import Path


def split_text_into_paragraphs_and_code(text):
    code_block_pattern = r"```(?:\w+)?\n(.*?)```"
    code_blocks = re.findall(code_block_pattern, text, flags=re.DOTALL)

    # Remove code blocks from the text
    text_no_code = re.sub(code_block_pattern, "", text, flags=re.DOTALL)
    paragraphs = [p.strip() for p in text_no_code.strip().split("\n") if p.strip()]

    return paragraphs, code_blocks

if __name__ == "__main__":
    input_path = Path(r"E:\Desktop\team-it-works-on-my-machine\genai\rag\data\processed\actions_docs_chunked.json")
    output_path = Path(r"E:\Desktop\team-it-works-on-my-machine\genai\rag\data\processed\structured_chunks.json")


    with input_path.open(encoding="utf-8") as f:
        raw_chunks = json.load(f)

    structured_chunks = []

    for chunk in raw_chunks:
        paragraphs, code_blocks = split_text_into_paragraphs_and_code(chunk["text"])
        structured_chunk = {
            "chunk_id": chunk.get("chunk_id"),
            "title": chunk.get("title"),
            "source_url": chunk.get("source_url"),
            "paragraphs": paragraphs,
            "code_blocks": code_blocks
        }
        structured_chunks.append(structured_chunk)

    with output_path.open("w", encoding="utf-8") as f:
        json.dump(structured_chunks, f, indent=2, ensure_ascii=False)

    print(f"✅ Done! {len(structured_chunks)} structured chunks saved to {output_path}")
