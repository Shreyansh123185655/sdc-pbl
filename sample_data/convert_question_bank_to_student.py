"""
Convert question-bank Excel format to student-row Excel format.

Input format (one question per row):
  Question | Option A | Option B | Option C | Option D | Correct Answer

Output format (one student per row):
  Name, EnrollmentNo, Q1, Q1_A, Q1_B, Q1_C, Q1_D, Q1_Answer, ...
"""

from __future__ import annotations

import argparse
from pathlib import Path

import pandas as pd


def _norm_col(name: str) -> str:
    return "".join(ch.lower() for ch in str(name).strip() if ch.isalnum())


def _pick_col(columns: list[str], *aliases: str) -> str:
    index = {_norm_col(c): c for c in columns}
    for alias in aliases:
        col = index.get(_norm_col(alias))
        if col:
            return col
    raise ValueError(f"Missing expected column. Tried aliases: {aliases}")


def convert(
    input_path: Path,
    output_path: Path,
    student_name: str,
    enrollment_no: str,
    max_questions: int,
) -> None:
    df = pd.read_excel(input_path, dtype=str).fillna("")
    cols = list(df.columns)

    q_col = _pick_col(cols, "Question")
    a_col = _pick_col(cols, "Option A", "OptionA")
    b_col = _pick_col(cols, "Option B", "OptionB")
    c_col = _pick_col(cols, "Option C", "OptionC")
    d_col = _pick_col(cols, "Option D", "OptionD")
    ans_col = _pick_col(cols, "Correct Answer", "CorrectAnswer", "Answer")

    rows = []
    for _, row in df.iterrows():
        question = str(row[q_col]).strip()
        if not question:
            continue
        rows.append(
            {
                "q": question,
                "a": str(row[a_col]).strip(),
                "b": str(row[b_col]).strip(),
                "c": str(row[c_col]).strip(),
                "d": str(row[d_col]).strip(),
                "ans": str(row[ans_col]).strip().upper(),
            }
        )

    if not rows:
        raise ValueError("No valid questions found in input file.")

    rows = rows[:max_questions]
    out: dict[str, str] = {"Name": student_name, "EnrollmentNo": enrollment_no}

    for i, q in enumerate(rows, start=1):
        out[f"Q{i}"] = q["q"]
        out[f"Q{i}_A"] = q["a"]
        out[f"Q{i}_B"] = q["b"]
        out[f"Q{i}_C"] = q["c"]
        out[f"Q{i}_D"] = q["d"]
        out[f"Q{i}_Answer"] = q["ans"]

    out_df = pd.DataFrame([out])
    output_path.parent.mkdir(parents=True, exist_ok=True)
    out_df.to_excel(output_path, index=False)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", required=True, help="Path to question-bank .xlsx file")
    parser.add_argument("--output", required=True, help="Path to output student-format .xlsx")
    parser.add_argument("--name", default="Student One", help="Student name for generated row")
    parser.add_argument("--enrollment", default="EN0001", help="Enrollment number")
    parser.add_argument(
        "--max-questions",
        type=int,
        default=20,
        help="Maximum questions to include (API supports up to 20)",
    )
    args = parser.parse_args()

    if args.max_questions < 10:
        raise ValueError("max-questions must be at least 10.")
    if args.max_questions > 20:
        raise ValueError("max-questions must be at most 20.")

    convert(
        input_path=Path(args.input),
        output_path=Path(args.output),
        student_name=args.name,
        enrollment_no=args.enrollment,
        max_questions=args.max_questions,
    )
    print(f"Created: {args.output}")


if __name__ == "__main__":
    main()

