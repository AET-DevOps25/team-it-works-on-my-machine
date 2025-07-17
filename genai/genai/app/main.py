import time
from typing import List

from fastapi import FastAPI, HTTPException
from langchain_core.utils import secret_from_env
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from dotenv import load_dotenv
from pydantic import BaseModel
from genai.rag.utils.retrieve_data import retrieve_text
import traceback
import uvicorn
from prometheus_fastapi_instrumentator import Instrumentator
import asyncio

load_dotenv()

app = FastAPI()
Instrumentator().instrument(app).expose(app)

# Define the input types for the YAML files
class YamlFile(BaseModel):
    filename: str
    content: str


class YamlRequest(BaseModel):
    yamls: List[YamlFile]


# Simple Q&A Chain
def create_chain():

    llm = ChatOpenAI(
        model_name="gpt-4o",
        openai_api_key=secret_from_env("OPENAI_API_KEY")(),
    )
    prompt = ChatPromptTemplate.from_template(
        "You are a professional assistant. Answer this question in English: {question}"
    )
    return prompt | llm


chain = create_chain()
@app.get("/ping")
async def ping():
    return "Pong from GenAI Service"


@app.post("/analyze-yamls")
async def analyze_yamls(payload: YamlRequest):
    async def analyze_yaml(i: int, y: YamlFile):
        try:
            # Delay to bypass rate limits
            await asyncio.sleep(i/4)

            filename = y.filename
            content = y.content

            # Step 1: LLM brief analysis
            summary_prompt = f"""You are a DevOps assistant. Analyze the following GitHub Actions workflow and summarize its purpose or functionality in 1-2 sentences:
    
    YAML file: {filename}
    Content:
    {content}
    """
            start = time.time()
            summary_response = await asyncio.to_thread(chain.invoke, {"question": summary_prompt})
            print(f"summary time: {time.time() - start:.2f} seconds")

            summary = summary_response.content.strip()

            # Step 2: RAG retrieval
            query_text = f"{summary}\n\n{content}"

            start = time.time()
            retrieved = await asyncio.to_thread(retrieve_text, query_text, collection_name="workflow_docs")
            print(f"rag retrieval time: {time.time() - start:.2f} seconds")

            # Step 3: Combine YAML content and RAG context for deeper analysis
            rag_snippets = []
            source_urls=[]
            for item in retrieved:
                payload = item.payload
                source_url = payload.get('source_url', '')
                if source_url in source_urls:
                    continue
                rag_snippets.append(
                    f"- {payload.get('title', 'Untitled')}: {payload.get('text', '')}")
                source_urls.append(source_url)

            rag_context_text = "\n".join(rag_snippets)

            # Aggregate results
            detailed_prompt = \
                f"""You are a GitHub Actions expert.
    Here is a GitHub Actions workflow file named `{filename}`:
    ```yaml
    {content}
            
    Based on the following related documentation snippets:
    
    {rag_context_text}
    
    Please summarize in detail what this workflow does and suggest improvements if applicable.
    """
            start = time.time()
            detailed_response = await asyncio.to_thread(chain.invoke, {"question": detailed_prompt})
            print(f"detailed analysis time: {time.time() - start:.2f} seconds")
            final_analysis = detailed_response.content.strip()

            return {
                "filename": filename,
                "summary": summary,
                "related_docs": source_urls,
                "detailed_analysis": final_analysis
            }
        except Exception as e:
            traceback.print_exc()
            raise HTTPException(status_code=500, detail=f"Error processing {y.filename}: {str(e)}")

    results = await asyncio.gather(*(analyze_yaml(i, y) for i, y in enumerate(payload.yamls)))
    return {"results": results}

def main():
    uvicorn.run("genai.app.main:app", host="0.0.0.0", port=int(3001), reload=True)

if __name__ == "__main__":
    main()
