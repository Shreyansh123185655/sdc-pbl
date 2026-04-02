# Excel File Converter Usage Guide

## Problem
Your Excel file has different column names than what the Student PDF Generator expects. The system requires:
- `Name` column for student names
- `EnrollmentNo` column for enrollment numbers  
- `Q1`, `Q2`, etc. for questions
- `Q1_A`, `Q1_B`, `Q1_C`, `Q1_D` for options
- `Q1_Answer` for correct answers

## Solution
Use the converter to transform your Excel file to the required format.

## Quick Usage

### Method 1: Simple Script (Recommended)
```bash
cd sample_data
./convert_file.sh your_excel_file.xlsx
```

### Method 2: Python Script
```bash
cd sample_data
python convert_excel.py input_file.xlsx output_file.xlsx
```

### Method 3: Direct Command
```bash
cd sample_data
source ../.venv/bin/activate
python convert_excel.py mcq_basic_sample.xlsx converted_students.xlsx
```

## What the Converter Does

1. **Detects Columns**: Automatically finds your name, enrollment, and question columns
2. **Maps to Standard Format**: Converts your column names to the required format
3. **Handles Variations**: Works with common column name variations like:
   - Name: `name`, `student_name`, `studentname`, `student`
   - Enrollment: `enrollment`, `enrollment_no`, `roll_no`, `id`
   - Questions: `Q1`, `q1`, `Question1`, etc.
4. **Validates Data**: Ensures minimum 10 questions per student
5. **Creates Proper Format**: Generates Excel file with exact column names required

## Example

If your file has:
```
StudentName | RollNumber | Q1Text | Q1OptA | Q1OptB | Q1OptC | Q1OptD | Q1Correct
```

The converter will create:
```
Name | EnrollmentNo | Q1 | Q1_A | Q1_B | Q1_C | Q1_D | Q1_Answer | Q2 | Q2_A | ...
```

## After Conversion

1. Upload the converted file to the Student PDF Generator
2. The system will successfully parse and generate PDFs
3. Each student gets an individual PDF with their questions

## Troubleshooting

- **Missing columns**: Ensure your file has name and enrollment columns
- **Too few questions**: Need at least 10 questions per student
- **Empty rows**: Converter skips rows with missing data
