from fastapi import FastAPI, Body
from langchain_openai import ChatOpenAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.utils import secret_from_env
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()


# Simple Q&A Chain
def create_chain():
    llm = ChatOpenAI(
        model_name="deepseek-chat",
        openai_api_key=secret_from_env("DEEPSEEK_API_KEY")(),
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

@app.get("/ping")
async def ping():
    return "Pong from GenAI Service"




if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="0.0.0.0", port=int(3001), reload=True)
