#!/bin/bash

# Universal Excel Converter CLI Tool
# Command-line interface for converting Excel files

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_SCRIPT="$SCRIPT_DIR/universal_excel_converter.py"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_usage() {
    echo "Universal Excel Converter - Command Line Tool"
    echo "======================================"
    echo ""
    echo "Usage: $0 [OPTIONS] <input_file> [output_file]"
    echo ""
    echo "Options:"
    echo "  -a, --analyze    Only analyze file structure, don't convert"
    echo "  -f, --force     Force conversion even if already compatible"
    echo "  -h, --help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 my_data.xlsx                    # Convert with auto-detection"
    echo "  $0 -a my_data.xlsx                # Analyze only"
    echo "  $0 -f my_data.xlsx converted.xlsx    # Force convert"
    echo ""
    echo "Supported Formats:"
    echo "  • Standard Student Format (Name, EnrollmentNo, Q1, Q1_A, etc.)"
    echo "  • Question Bank Format (QuestionText, OptionA, OptionB, etc.)"
    echo "  • Simple Student Format (Name, Roll, basic columns)"
    echo "  • Tabular Format (Any Excel table structure)"
    echo "  • Custom Format (Automatic detection and conversion)"
}

print_header() {
    echo -e "${BLUE}🔍 Universal Excel Converter${NC}"
    echo "================================"
}

print_error() {
    echo -e "${RED}❌ Error: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ Success: $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check if Python script exists
if [ ! -f "$PYTHON_SCRIPT" ]; then
    print_error "universal_excel_converter.py not found in $SCRIPT_DIR"
    exit 1
fi

# Parse command line arguments
ANALYZE_ONLY=false
FORCE_CONVERT=false
INPUT_FILE=""
OUTPUT_FILE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -a|--analyze)
            ANALYZE_ONLY=true
            shift
            ;;
        -f|--force)
            FORCE_CONVERT=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        -*)
            print_error "Unknown option: $1"
            print_usage
            exit 1
            ;;
        *)
            if [ -z "$INPUT_FILE" ]; then
                INPUT_FILE="$1"
            elif [ -z "$OUTPUT_FILE" ]; then
                OUTPUT_FILE="$1"
            else
                print_error "Too many arguments"
                print_usage
                exit 1
            fi
            shift
            ;;
    esac
done

# Check if input file provided
if [ -z "$INPUT_FILE" ]; then
    print_error "Input file is required"
    print_usage
    exit 1
fi

# Check if input file exists
if [ ! -f "$INPUT_FILE" ]; then
    print_error "Input file not found: $INPUT_FILE"
    exit 1
fi

print_header
print_info "Processing file: $INPUT_FILE"

# Build Python command
PYTHON_CMD="python3"
if ! command -v python3 &> /dev/null; then
    PYTHON_CMD="python"
fi

# Prepare arguments
PYTHON_ARGS="$INPUT_FILE"
if [ -n "$OUTPUT_FILE" ]; then
    PYTHON_ARGS="$PYTHON_ARGS $OUTPUT_FILE"
fi

if [ "$FORCE_CONVERT" = true ]; then
    PYTHON_ARGS="$PYTHON_ARGS --force"
fi

if [ "$ANALYZE_ONLY" = true ]; then
    print_info "Analyzing file structure..."
    $PYTHON_CMD "$PYTHON_SCRIPT" "$PYTHON_ARGS"
    ANALYSIS_RESULT=$?
    
    if [ $ANALYSIS_RESULT -eq 0 ]; then
        echo ""
        print_success "Analysis completed successfully"
        echo ""
        print_info "To convert the file, run:"
        echo "  $0 $INPUT_FILE $([ -n "$OUTPUT_FILE" ] && echo "$OUTPUT_FILE" || echo "converted_$(basename "$INPUT_FILE")")"
    else
        echo ""
        print_error "Analysis failed"
        exit 1
    fi
else
    print_info "Converting Excel file..."
    $PYTHON_CMD "$PYTHON_SCRIPT" "$PYTHON_ARGS"
    CONVERSION_RESULT=$?
    
    if [ $CONVERSION_RESULT -eq 0 ]; then
        echo ""
        print_success "Conversion completed successfully"
        
        # Show output file info
        if [ -n "$OUTPUT_FILE" ]; then
            OUTPUT_PATH="$OUTPUT_FILE"
        else
            OUTPUT_PATH="converted_$(basename "$INPUT_FILE")"
        fi
        
        if [ -f "$OUTPUT_PATH" ]; then
            FILE_SIZE=$(stat -f%z "$OUTPUT_PATH" 2>/dev/null || stat -c%s "$OUTPUT_PATH" 2>/dev/null)
            print_info "Output file: $OUTPUT_PATH (${FILE_SIZE} bytes)"
        fi
        
        echo ""
        print_info "You can now use the converted file with Student PDF Generator"
        echo "  • Upload via web interface: http://localhost:8000/ui"
        echo "  • Or use directly: curl -X POST -F 'file=@$OUTPUT_PATH' http://localhost:8000/upload"
    else
        echo ""
        print_error "Conversion failed"
        exit 1
    fi
fi

echo ""
print_info "For more help, run: $0 --help"
