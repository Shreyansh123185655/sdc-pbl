package com.studentpdf.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response returned to FastAPI after PDF generation.
 */
@Data
@Builder
public class GenerateResponse {
    private String                 status;          // "SUCCESS" | "PARTIAL" | "FAILURE"
    private String                 message;
    private int                    totalRequested;
    private int                    successCount;
    private int                    failureCount;
    private String                 outputDirectory;
    private List<GeneratedFileInfo> generatedFiles;
    private List<String>           errors;
    private LocalDateTime          timestamp;
}
