package com.studentpdf.model;

import lombok.Builder;
import lombok.Data;

/**
 * Metadata about one successfully generated PDF file.
 */
@Data
@Builder
public class GeneratedFileInfo {
    private String studentName;
    private String enrollmentNo;
    private String fileName;
    private String filePath;
    private int    questionCount;
    private long   fileSizeBytes;
}
