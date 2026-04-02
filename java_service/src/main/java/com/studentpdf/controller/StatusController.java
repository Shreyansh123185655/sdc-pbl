package com.studentpdf.controller;

import com.studentpdf.config.PdfOutputConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight status/health controller.
 *
 * <p>Used by FastAPI's {@code GET /java-status} probe and by the
 * HTML frontend's service-health indicator.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class StatusController {

    private final PdfOutputConfig outputConfig;

    /**
     * GET /api/status
     * Returns service metadata and output-directory stats.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Path   outputPath = outputConfig.getOutputPath();
        File   outputDir  = outputPath.toFile();

        long existingPdfCount = 0;
        long totalSizeBytes   = 0;

        if (outputDir.exists() && outputDir.isDirectory()) {
            File[] pdfs = outputDir.listFiles((d, name) -> name.endsWith(".pdf"));
            if (pdfs != null) {
                existingPdfCount = pdfs.length;
                for (File f : pdfs) totalSizeBytes += f.length();
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("service",          "student-pdf-service");
        body.put("version",          "1.0.0");
        body.put("status",           "UP");
        body.put("timestamp",        LocalDateTime.now().toString());
        body.put("outputDirectory",  outputPath.toAbsolutePath().toString());
        body.put("outputDirExists",  outputDir.exists());
        body.put("outputDirWritable",outputDir.canWrite());
        body.put("existingPdfCount", existingPdfCount);
        body.put("totalSizeMB",      String.format("%.2f", totalSizeBytes / (1024.0 * 1024.0)));

        log.debug("Status check — outputDir={}, pdfs={}", outputPath, existingPdfCount);
        return ResponseEntity.ok(body);
    }

    /**
     * GET /api/files
     * Lists all PDF files currently in the output directory.
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> listFiles() {
        File outputDir = outputConfig.getOutputPath().toFile();
        Map<String, Object> body = new HashMap<>();

        if (!outputDir.exists() || !outputDir.isDirectory()) {
            body.put("error", "Output directory does not exist: " + outputDir.getAbsolutePath());
            return ResponseEntity.status(500).body(body);
        }

        File[] pdfs = outputDir.listFiles((d, name) -> name.endsWith(".pdf"));
        java.util.List<Map<String, Object>> fileList = new java.util.ArrayList<>();

        if (pdfs != null) {
            for (File f : pdfs) {
                Map<String, Object> fm = new HashMap<>();
                fm.put("fileName",    f.getName());
                fm.put("filePath",    f.getAbsolutePath());
                fm.put("sizeBytes",   f.length());
                fm.put("lastModified",new java.util.Date(f.lastModified()).toString());
                fileList.add(fm);
            }
        }

        body.put("outputDirectory", outputDir.getAbsolutePath());
        body.put("totalPdfs",       fileList.size());
        body.put("files",           fileList);

        return ResponseEntity.ok(body);
    }
}
