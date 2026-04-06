from fastapi import FastAPI
from fastapi.responses import JSONResponse
import uvicorn

app = FastAPI(title="Student PDF Generator - Minimal Test")

@app.get("/")
async def root():
    return {"message": "Student PDF Generator Backend is Running!", "status": "ok"}

@app.get("/health")
async def health():
    return {
        "status": "ok",
        "service": "student-pdf-generator-backend-minimal",
        "version": "1.0.0-test",
        "timestamp": "2026-04-06T14:23:00.000Z"
    }

@app.get("/test")
async def test():
    return {"test": "successful", "deployment": "working"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
