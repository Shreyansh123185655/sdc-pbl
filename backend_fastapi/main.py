"""
FastAPI Backend — Student PDF Generator
Entry point: uvicorn main:app --reload --port 8000
"""
from __future__ import annotations

import io
import json
import logging
import os
import random
import sys
from pathlib import Path
from typing import Any, List, Dict, Optional

# Add sample_data directory to path for importing converter
sys.path.append(str(Path(__file__).parent.parent / "sample_data"))

try:
    from universal_excel_converter import analyze_and_convert_excel
    UNIVERSAL_CONVERTER_AVAILABLE = True
except ImportError:
    UNIVERSAL_CONVERTER_AVAILABLE = False
    logger.warning("Universal converter not available - falling back to standard parsing")

import httpx
import pandas as pd
import uvicorn
from fastapi import FastAPI, File, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi.staticfiles import StaticFiles

# ── Logging ──────────────────────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s  %(levelname)-8s  %(name)s  %(message)s",
)
log = logging.getLogger("student-pdf")

# ── Config ───────────────────────────────────────────────────────────────────
JAVA_SERVICE_URL = os.getenv("JAVA_SERVICE_URL", "http://localhost:8081")
JAVA_GENERATE_ENDPOINT = f"{JAVA_SERVICE_URL}/api/generate"
JAVA_STATUS_ENDPOINT   = f"{JAVA_SERVICE_URL}/api/status"
JAVA_TIMEOUT           = float(os.getenv("JAVA_TIMEOUT", "120"))   # seconds
MIN_QUESTIONS = 10
MAX_QUESTIONS = 20

# ── App ───────────────────────────────────────────────────────────────────────
app = FastAPI(
    title="Student PDF Generator API",
    description="Parses Excel → validates → sends to Java service for PDF generation",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Serve static frontend
FRONTEND_DIR = Path(__file__).parent.parent / "frontend"
if FRONTEND_DIR.exists():
    app.mount("/ui", StaticFiles(directory=str(FRONTEND_DIR), html=True), name="frontend")


# ── Helpers ──────────────────────────────────────────────────────────────────
def _parse_excel_bytes(file_bytes: bytes) -> list[dict]:
    """Parse Excel from bytes (for converted files)"""
    df = pd.read_excel(io.BytesIO(file_bytes), dtype=str)
    return df.to_dict(orient="records")


def _parse_excel(file: UploadFile) -> list[dict[str, Any]]:
    """Read Excel bytes → list of raw row dicts."""
    df = pd.read_excel(io.BytesIO(file.file), dtype=str)
    df.columns = [c.strip() for c in df.columns]
    df.dropna(how="all", inplace=True)
    return df.to_dict(orient="records")


def _validate_and_structure(rows: list[dict]) -> list[dict]:
    """Validate rows and build structured student JSON list."""
    students: list[dict] = []
    errors: list[str] = []

    for idx, row in enumerate(rows, start=2):  # row 1 = header
        name   = str(row.get("Name",         "")).strip()
        enroll = str(row.get("EnrollmentNo", "")).strip()

        if not name:
            errors.append(f"Row {idx}: 'Name' is missing.")
        if not enroll:
            errors.append(f"Row {idx}: 'EnrollmentNo' is missing.")
        if errors:
            continue  # collect all errors first

        # Detect question columns  Q1, Q2 … Q20
        questions: list[dict] = []
        q_num = 1
        while q_num <= MAX_QUESTIONS:
            q_key = f"Q{q_num}"
            if q_key not in row or pd.isna(row[q_key]) or str(row[q_key]).strip() in ("", "nan"):
                break
            q_text = str(row[q_key]).strip()
            options: dict[str, str] = {}
            for opt in ("A", "B", "C", "D"):
                opt_key = f"Q{q_num}_{opt}"
                val = str(row.get(opt_key, "")).strip()
                if val and val != "nan":
                    options[opt] = val
            answer = str(row.get(f"Q{q_num}_Answer", "")).strip().upper()

            if len(options) < 2:
                errors.append(
                    f"Row {idx}: Question {q_num} has fewer than 2 options."
                )
            questions.append(
                {"number": q_num, "text": q_text, "options": options, "answer": answer}
            )
            q_num += 1

        q_count = len(questions)
        if q_count < MIN_QUESTIONS:
            errors.append(
                f"Row {idx}: Student '{name}' has only {q_count} questions "
                f"(minimum {MIN_QUESTIONS} required)."
            )
        if q_count > MAX_QUESTIONS:
            errors.append(
                f"Row {idx}: Student '{name}' has {q_count} questions "
                f"(maximum {MAX_QUESTIONS} allowed)."
            )

        students.append(
            {
                "name":         name,
                "enrollmentNo": enroll,
                "questions":    questions,
            }
        )

    if errors:
        raise ValueError("\n".join(errors))

    if not students:
        raise ValueError("No valid student records found in the uploaded file.")

    return students


def _shuffle_and_distribute_students(students: List[Dict], config: Dict) -> List[Dict]:
    """Shuffle and distribute questions based on configuration"""
    shuffle_mode = config.get('shuffle_mode', 'none')
    num_students = config.get('num_students', len(students))
    questions_per_student = config.get('questions_per_student', 15)
    question_start = config.get('question_start', 1)
    question_end = config.get('question_end', 20)
    
    log.info(f"Shuffle mode: {shuffle_mode}, Students: {num_students}, Questions per student: {questions_per_student}")
    
    # Create student names if we need more students
    base_names = ["Aarav Sharma", "Priya Patel", "Rohit Verma", "Sneha Gupta", "Arjun Singh",
                  "Kavya Reddy", "Rahul Kumar", "Anjali Singh", "Vikram Malhotra", "Divya Sharma"]
    
    new_students = []
    for i in range(num_students):
        if i < len(students):
            base_student = students[i]
        else:
            # Use a base student's questions but create new student info
            base_student = students[0] if students else {'questions': []}
        
        # Extract questions in the specified range
        all_questions = base_student.get('questions', [])
        range_questions = []
        
        for q in all_questions:
            q_num = q.get('number', 1)
            if question_start <= q_num <= question_end:
                range_questions.append(q)
        
        # Limit questions to specified range
        if len(range_questions) > questions_per_student:
            if shuffle_mode == 'random_subset':
                # Random subset of questions
                range_questions = random.sample(range_questions, questions_per_student)
            else:
                # Take first N questions in range
                range_questions = range_questions[:questions_per_student]
        
        # Shuffle order if requested
        if shuffle_mode == 'random':
            random.shuffle(range_questions)
        
        # Create student with proper numbering
        if i < len(students):
            new_student = {
                'name': base_student['name'],
                'enrollmentNo': base_student['enrollmentNo'],
                'questions': []
            }
        else:
            # Generate new student info
            name_idx = i % len(base_names)
            new_student = {
                'name': base_names[name_idx],
                'enrollmentNo': f"EN2024{str(i+1).zfill(3)}",
                'questions': []
            }
        
        # Re-number questions sequentially
        for j, q in enumerate(range_questions, 1):
            new_question = q.copy()
            new_question['number'] = j
            new_student['questions'].append(new_question)
        
        new_students.append(new_student)
    
    return new_students


@app.post("/upload-advanced", summary="Upload Excel with advanced configuration")
async def upload_excel_advanced(
    file: UploadFile = File(...),
    num_students: int = 5,
    questions_per_student: int = 15,
    question_start: int = 1,
    question_end: int = 20,
    shuffle_mode: str = "none"
):
    """Advanced upload with configuration for shuffling and distribution"""
    
    # Validate configuration
    if questions_per_student < 10 or questions_per_student > 20:
        raise HTTPException(status_code=400, detail="Questions per student must be between 10 and 20")
    
    if question_start > question_end:
        raise HTTPException(status_code=400, detail="Question start must be less than or equal to question end")
    
    if num_students < 1 or num_students > 50:
        raise HTTPException(status_code=400, detail="Number of students must be between 1 and 50")
    
    if shuffle_mode not in ["none", "random", "random_subset"]:
        raise HTTPException(status_code=400, detail="Invalid shuffle mode")
    
    # Same validation as regular upload
    allowed_exts = {".xlsx", ".xls"}
    ext = Path(file.filename or "").suffix.lower()
    
    if ext not in allowed_exts:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid file extension '{ext}'. Only .xlsx and .xls are accepted.",
        )
    
    file_bytes = await file.read()
    log.info("Received advanced upload file '%s' (%d bytes)", file.filename, len(file_bytes))
    
    # Parse Excel
    try:
        raw_rows = _parse_excel(file_bytes)
    except Exception as exc:
        log.error("Excel parse error: %s", exc)
        raise HTTPException(status_code=400, detail=f"Failed to parse Excel file: {exc}")
    
    if not raw_rows:
        raise HTTPException(status_code=400, detail="Excel file has no data rows.")
    
    # Validate and structure
    try:
        students = _validate_and_structure(raw_rows)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc))
    
    log.info("Validated %d student(s) from original file.", len(students))
    
    # Apply shuffling and distribution
    config = {
        'shuffle_mode': shuffle_mode,
        'num_students': num_students,
        'questions_per_student': questions_per_student,
        'question_start': question_start,
        'question_end': question_end
    }
    
    processed_students = _shuffle_and_distribute_students(students, config)
    
    log.info("Processed %d student(s) with configuration.", len(processed_students))
    
    # Send to Java service
    result = await _call_java_service(processed_students)
    
    log.info("Java service response: %s", json.dumps(result, indent=2))
    
    return JSONResponse(
        status_code=200,
        content={
            "message": "PDFs generated successfully with advanced configuration.",
            "configuration": config,
            "originalStudentCount": len(students),
            "processedStudentCount": len(processed_students),
            "generatedFiles": result.get("generatedFiles", []),
            "outputDirectory": result.get("outputDirectory", ""),
            "javaDetail": result,
        },
    )


async def _call_java_service(students: list[dict]) -> dict:
    payload = {"students": students}
    log.info("Sending %d student(s) to Java service …", len(students))

    async with httpx.AsyncClient(timeout=JAVA_TIMEOUT) as client:
        try:
            resp = await client.post(JAVA_GENERATE_ENDPOINT, json=payload)
            resp.raise_for_status()
            return resp.json()
        except httpx.ConnectError:
            raise HTTPException(
                status_code=503,
                detail=(
                    "Java PDF service is unreachable. "
                    f"Ensure it is running at {JAVA_SERVICE_URL}."
                ),
            )
        except httpx.TimeoutException:
            raise HTTPException(
                status_code=504,
                detail="Java service timed out while generating PDFs.",
            )
        except httpx.HTTPStatusError as exc:
            try:
                detail = exc.response.json().get("message", exc.response.text)
            except Exception:
                detail = exc.response.text
            raise HTTPException(
                status_code=exc.response.status_code,
                detail=f"Java service error: {detail}",
            )


# ── Routes ────────────────────────────────────────────────────────────────────
@app.get("/", summary="API root")
async def root():
    return {
        "service": "Student PDF Generator",
        "version": "1.0.0",
        "endpoints": {
            "upload":  "POST /upload",
            "health":  "GET  /health",
            "java_status": "GET /java-status",
            "ui":      "/ui  (HTML frontend)",
        },
    }


@app.get("/health", summary="Liveness probe")
async def health():
    return {"status": "ok"}


@app.get("/java-status", summary="Check Java service connectivity")
async def java_status():
    async with httpx.AsyncClient(timeout=5) as client:
        try:
            resp = await client.get(JAVA_STATUS_ENDPOINT)
            resp.raise_for_status()
            return {"java_service": "reachable", "detail": resp.json()}
        except Exception as exc:
            return {"java_service": "unreachable", "error": str(exc)}

@app.get("/java-info", summary="Get Java service info")
async def java_info():
    """Return Java service URL and info for frontend"""
    return {
        "java_service_url": JAVA_SERVICE_URL,
        "java_files_endpoint": f"{JAVA_SERVICE_URL}/api/files",
        "java_download_endpoint": f"{JAVA_SERVICE_URL}/api/download/"
    }


@app.get("/converter-info", summary="Get universal converter status")
async def converter_info():
    """Get information about the universal Excel converter"""
    return {
        "universal_converter_available": UNIVERSAL_CONVERTER_AVAILABLE,
        "supported_formats": [
            "standard_student_format",
            "question_bank_format", 
            "simple_student_format",
            "tabular_format",
            "custom_format"
        ],
        "auto_conversion_enabled": True,
        "fallback_to_standard": True
    }


@app.post("/analyze-excel", summary="Analyze Excel file structure")
async def analyze_excel(file: UploadFile = File(...)):
    """Analyze Excel file structure without converting"""
    if not UNIVERSAL_CONVERTER_AVAILABLE:
        raise HTTPException(
            status_code=503, 
            detail="Universal converter not available"
        )
    
    # Save uploaded file temporarily
    temp_file = Path(f"temp_analysis_{file.filename}")
    try:
        content = await file.read()
        with open(temp_file, "wb") as f:
            f.write(content)
        
        # Analyze the file
        result = analyze_and_convert_excel(str(temp_file), force_convert=False)
        
        # Clean up
        temp_file.unlink(missing_ok=True)
        
        return JSONResponse(
            status_code=200,
            content=result
        )
        
    except Exception as exc:
        # Clean up on error
        temp_file.unlink(missing_ok=True)
        log.error("Excel analysis error: %s", exc)
        raise HTTPException(
            status_code=400, 
            detail=f"Failed to analyze Excel file: {exc}"
        )


@app.post("/convert-excel", summary="Convert Excel file to standard format")
async def convert_excel(file: UploadFile = File(...)):
    """Convert any Excel file to standard Student PDF Generator format"""
    if not UNIVERSAL_CONVERTER_AVAILABLE:
        raise HTTPException(
            status_code=503, 
            detail="Universal converter not available"
        )
    
    # Save uploaded file temporarily
    temp_input = Path(f"temp_input_{file.filename}")
    temp_output = Path(f"converted_{file.filename}")
    
    try:
        content = await file.read()
        with open(temp_input, "wb") as f:
            f.write(content)
        
        # Convert the file
        result = analyze_and_convert_excel(str(temp_input), str(temp_output), force_convert=True)
        
        # Clean up input file
        temp_input.unlink(missing_ok=True)
        
        if result.get("conversion_success", False):
            # Return converted file
            with open(temp_output, "rb") as f:
                converted_content = f.read()
            
            # Clean up output file after reading
            temp_output.unlink(missing_ok=True)
            
            return JSONResponse(
                status_code=200,
                content={
                    "message": "File converted successfully",
                    "analysis": result.get("analysis", {}),
                    "converted_file_size": len(converted_content),
                    "original_filename": file.filename,
                    "converted_filename": f"converted_{file.filename}"
                }
            )
        else:
            raise HTTPException(
                status_code=400,
                detail=f"Conversion failed: {result.get('message', 'Unknown error')}"
            )
        
    except HTTPException:
        raise
    except Exception as exc:
        # Clean up on error
        temp_input.unlink(missing_ok=True)
        temp_output.unlink(missing_ok=True)
        log.error("Excel conversion error: %s", exc)
        raise HTTPException(
            status_code=400, 
            detail=f"Failed to convert Excel file: {exc}"
        )


@app.post("/upload", summary="Upload Excel → generate PDFs")
async def upload_excel(file: UploadFile = File(...)):
    # ── 1. Validate file type ─────────────────────────────────────────────
    allowed_types = {
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-excel",
    }
    allowed_exts = {".xlsx", ".xls"}
    ext = Path(file.filename or "").suffix.lower()

    if ext not in allowed_exts:
        raise HTTPException(
            status_code=400,
            detail=f"Invalid file extension '{ext}'. Only .xlsx and .xls are accepted.",
        )

    file_bytes = await file.read()
    log.info("Received file '%s'  (%d bytes)", file.filename, len(file_bytes))

    # ── 2. Try Universal Converter First (if available) ───────────────────────
    if UNIVERSAL_CONVERTER_AVAILABLE:
        try:
            # Save file temporarily for conversion
            temp_input = Path(f"temp_upload_{file.filename}")
            temp_output = Path(f"converted_upload_{file.filename}")
            
            with open(temp_input, "wb") as f:
                f.write(file_bytes)
            
            # Try to analyze and convert
            result = analyze_and_convert_excel(temp_input, str(temp_output), force_convert=False)
            
            # Clean up input file
            temp_input.unlink(missing_ok=True)
            
            if result.get("conversion_success", False) or not result.get("needs_conversion", False):
                # File is already compatible or was successfully converted
                if result.get("needs_conversion", False):
                    # Read converted file
                    try:
                        with open(temp_output, "rb") as f:
                            file_bytes = f.read()
                        log.info("File converted using universal converter")
                    except Exception as e:
                        log.error(f"Failed to read converted file: {e}")
                        # Fall back to original bytes
                        file_bytes = file_bytes
                else:
                    # Use original bytes
                    pass
                
                # Clean up output file
                temp_output.unlink(missing_ok=True)
                
                # Continue with standard parsing
                try:
                    raw_rows = _parse_excel_bytes(file_bytes)
                except Exception as exc:
                    log.error("Excel parse error after conversion: %s", exc)
                    raise HTTPException(status_code=400, detail=f"Failed to parse converted Excel file: {exc}")
            else:
                # Fallback to standard parsing
                log.warning("Universal converter could not process file, falling back to standard parsing")
                temp_output.unlink(missing_ok=True)
                raw_rows = _parse_excel_bytes(file_bytes)
                
        except Exception as exc:
            log.warning("Universal converter error, falling back to standard parsing: %s", exc)
            raw_rows = _parse_excel_bytes(file_bytes)
    else:
        # Use standard parsing
        raw_rows = _parse_excel_bytes(file_bytes)

    if not raw_rows:
        raise HTTPException(status_code=400, detail="Excel file has no data rows.")

    # ── 3. Validate & structure ───────────────────────────────────────────
    try:
        students = _validate_and_structure(raw_rows)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc))

    log.info("Validated %d student(s).", len(students))

    # ── 4. Send to Java service ───────────────────────────────────────────
    result = await _call_java_service(students)

    log.info("Java service response: %s", json.dumps(result, indent=2))

    return JSONResponse(
        status_code=200,
        content={
            "message":        "PDFs generated successfully.",
            "studentCount":   len(students),
            "generatedFiles": result.get("generatedFiles", []),
            "outputDirectory":result.get("outputDirectory", ""),
            "javaDetail":     result,
        },
    )


# ── Dev entry-point ──────────────────────────────────────────────────────────
if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
