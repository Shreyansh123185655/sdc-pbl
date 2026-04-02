package com.studentpdf.exception;

/**
 * Thrown when PDF generation fails for a specific student.
 */
public class PdfGenerationException extends RuntimeException {

    private final String enrollmentNo;

    public PdfGenerationException(String enrollmentNo, String message, Throwable cause) {
        super(message, cause);
        this.enrollmentNo = enrollmentNo;
    }

    public PdfGenerationException(String enrollmentNo, String message) {
        super(message);
        this.enrollmentNo = enrollmentNo;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }
}
