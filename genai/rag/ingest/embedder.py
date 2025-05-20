import json
from pathlib import Path
from typing import Union, List, Dict
from openai import OpenAI, OpenAIError
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm


def embed_record(record: Dict, client: OpenAI, model: str) -> Dict:
    try:
        response = client.embeddings.create(
            model=model,
            input=record["content"]
        )
        record["embedding"] = response.data[0].embedding
    except OpenAIError as e:
        print(f"❌ Error embedding {record['chunk_id']}: {e}")
        record["embedding"] = None
    return record


def embed_structured_chunks_concurrent(
        input_file: Union[str, Path],
        output_file: Union[str, Path],
        model: str = "text-embedding-3-small",
        max_workers: int = 5,
        max_records: int = None,
        api_key: str = None
):
    input_path = Path(input_file)
    output_path = Path(output_file)
    client = OpenAI(api_key=api_key)

    with input_path.open(encoding="utf-8") as f:
        chunks = json.load(f)

    records_to_embed = []

    for chunk in chunks:
        for i, para in enumerate(chunk.get("paragraphs", [])):
            records_to_embed.append({
                "chunk_id": f'{chunk["chunk_id"]}-para-{i}',
                "type": "paragraph",
                "content": para,
                "title": chunk.get("title", ""),
                "source_url": chunk.get("source_url", "")
            })
        for i, code in enumerate(chunk.get("code_blocks", [])):
            if not code.strip():
                continue  # 跳过空代码块
            records_to_embed.append({
                "chunk_id": f'{chunk["chunk_id"]}-code-{i}',
                "type": "code",
                "content": code,
                "title": chunk.get("title", ""),
                "source_url": chunk.get("source_url", "")
            })

    if max_records:
        records_to_embed = records_to_embed[:max_records]

    print(f"🔹 Total records to embed: {len(records_to_embed)} (using {max_workers} threads)")

    embedded_records: List[Dict] = []
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = [
            executor.submit(embed_record, record, client, model)
            for record in records_to_embed
        ]
        for future in tqdm(as_completed(futures), total=len(futures), desc="Embedding"):
            embedded_records.append(future.result())

    output_path.parent.mkdir(parents=True, exist_ok=True)
    with output_path.open("w", encoding="utf-8") as f:
        json.dump(embedded_records, f, indent=2, ensure_ascii=False)

    print(f"✅ Saved embedded records to {output_path}")


if __name__ == "__main__":
    input_path = Path(r"E:\Desktop\team-it-works-on-my-machine\genai\rag\data\processed\structured_chunks.json")
    output_path = Path(r"E:\Desktop\team-it-works-on-my-machine\genai\rag\data\processed\embedded_chunks.json")

    embed_structured_chunks_concurrent(
        input_file=input_path,
        output_file=output_path,
        model="text-embedding-3-small",
        # sleep_sec=0.5,
        max_workers=5  # 并发线程数
    )
