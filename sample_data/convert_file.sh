#!/bin/bash

# Excel Converter for Student PDF Generator
# This script converts any Excel file to the required format

echo "🔧 Excel File Converter for Student PDF Generator"
echo "=================================================="

# Check if input file is provided
if [ $# -eq 0 ]; then
    echo "Usage: ./convert_file.sh your_excel_file.xlsx"
    echo ""
    echo "This will convert your Excel file to the standard format required by the Student PDF Generator."
    echo "The converted file will be saved as 'converted_students.xlsx' in the same directory."
    exit 1
fi

INPUT_FILE="$1"
OUTPUT_FILE="converted_students.xlsx"

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    echo "❌ Error: Input file '$INPUT_FILE' not found!"
    exit 1
fi

# Check if it's an Excel file
if [[ ! "$INPUT_FILE" =~ \.(xlsx|xls)$ ]]; then
    echo "❌ Error: Input file must be an Excel file (.xlsx or .xls)"
    exit 1
fi

echo "📁 Input file: $INPUT_FILE"
echo "📁 Output file: $OUTPUT_FILE"
echo ""

# Activate virtual environment and run converter
cd "$(dirname "$0")/../.."
source .venv/bin/activate

python sample_data/convert_excel.py "$INPUT_FILE" "$OUTPUT_FILE"

echo ""
echo "✅ Conversion process completed!"
echo "📋 You can now upload '$OUTPUT_FILE' to the Student PDF Generator"
