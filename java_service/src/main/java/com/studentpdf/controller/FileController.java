package com.studentpdf.controller;

import com.studentpdf.config.PdfOutputConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;

/**
 * Allows callers to download individual generated PDFs by filename.
 *
 * <p>Endpoint: {@code GET /api/download/{fileName}}
 *
 * <p>Security: Only files inside the configured output directory
 * are served (path-traversal guard).
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final PdfOutputConfig outputConfig;

    /**
     * Download a generated PDF by its filename.
     *
     * @param fileName  e.g. {@code EN2024001_Aarav_Sharma.pdf}
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String fileName) {
        // ── Path-traversal guard ──────────────────────────────────────────────
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            log.warn("Rejected suspicious download request: {}", fileName);
            return ResponseEntity.badRequest().build();
        }

        Path outputDir  = outputConfig.getOutputPath();
        Path targetPath = outputDir.resolve(fileName).normalize();

        // Ensure resolved path is still inside the output directory
        if (!targetPath.startsWith(outputDir)) {
            log.warn("Path traversal attempt detected for: {}", fileName);
            return ResponseEntity.status(403).build();
        }

        File file = targetPath.toFile();
        if (!file.exists() || !file.isFile()) {
            log.warn("Requested file not found: {}", targetPath);
            return ResponseEntity.notFound().build();
        }

        log.info("Serving file: {}", targetPath);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(resource);
    }
}
