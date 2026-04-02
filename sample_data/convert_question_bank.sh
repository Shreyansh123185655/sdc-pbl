#!/bin/bash

# Question Bank to Student Format Converter
echo "🎓 Question Bank to Student Format Converter"
echo "=========================================="

# Check if input file is provided
if [ $# -eq 0 ]; then
    echo "Usage: ./convert_question_bank.sh your_question_bank.xlsx [number_of_students]"
    echo ""
    echo "Example:"
    echo "  ./convert_question_bank.sh mcq_basic_sample.xlsx 5"
    echo ""
    echo "This will convert your question bank to student format"
    echo "and create 'converted_students.xlsx' file."
    exit 1
fi

INPUT_FILE="$1"
OUTPUT_FILE="converted_students.xlsx"
NUM_STUDENTS=${2:-5}

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    echo "❌ Error: Input file '$INPUT_FILE' not found!"
    exit 1
fi

echo "📁 Input file: $INPUT_FILE"
echo "📁 Output file: $OUTPUT_FILE"
echo "👥 Number of students: $NUM_STUDENTS"
echo ""

# Activate virtual environment and run converter
cd "$(dirname "$0")/../.."
source .venv/bin/activate

python sample_data/convert_question_bank.py "$INPUT_FILE" "$OUTPUT_FILE" "$NUM_STUDENTS"

echo ""
echo "✅ Conversion completed!"
echo "📋 Now you can upload '$OUTPUT_FILE' to the Student PDF Generator"
