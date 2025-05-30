from fastapi import FastAPI, Body
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from dotenv import load_dotenv
import os

load_dotenv()

app = FastAPI()


# Simple Q&A Chain
def create_chain():
    llm = ChatOpenAI(
        model="deepseek-chat",
        openai_api_key=os.getenv("DEEPSEEK_API_KEY"),
        openai_api_base="https://api.deepseek.com/v1",
    )
    prompt = ChatPromptTemplate.from_template(
        "You are a professional assistant. Answer this question in English: {question}"
    )
    return prompt | llm


chain = create_chain()


@app.post("/ask")
async def ask_question(payload: dict = Body(...)):
    question = payload["question"]
    response = chain.invoke({"question": question})
    return {"response": response.content}


if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="0.0.0.0", port=int(os.getenv('GENAI_PORT')), reload=True)
