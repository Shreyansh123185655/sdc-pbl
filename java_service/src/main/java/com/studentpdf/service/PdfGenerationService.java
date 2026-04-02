package com.studentpdf.service;

import com.studentpdf.config.PdfOutputConfig;
import com.studentpdf.exception.PdfGenerationException;
import com.studentpdf.model.GeneratedFileInfo;
import com.studentpdf.model.GenerateRequest;
import com.studentpdf.model.GenerateResponse;
import com.studentpdf.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the full PDF generation pipeline for a batch of students.
 *
 * <p>For each student it:
 * <ol>
 *   <li>Resolves a safe output path via {@link FileNamingService}</li>
 *   <li>Creates a blank PDDocument</li>
 *   <li>Delegates layout/drawing to {@link PdfLayoutService}</li>
 *   <li>Saves the document permanently to the configured output directory</li>
 *   <li>Collects metadata ({@link GeneratedFileInfo}) for each successful save</li>
 * </ol>
 *
 * <p>Errors for individual students are captured and returned in the response
 * rather than aborting the entire batch.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerationService {

    private final PdfOutputConfig  outputConfig;
    private final FileNamingService fileNamingService;
    private final PdfLayoutService  pdfLayoutService;

    /**
     * Process an entire {@link GenerateRequest} and return a consolidated response.
     */
    public GenerateResponse generateAll(GenerateRequest request) {
        List<Student>           students       = request.getStudents();
        List<GeneratedFileInfo> generated      = new ArrayList<>();
        List<String>            errors         = new ArrayList<>();
        Path                    outputDir      = outputConfig.getOutputPath();

        log.info("Starting PDF generation for {} student(s). Output dir: {}",
                students.size(), outputDir);

        for (Student student : students) {
            try {
                GeneratedFileInfo info = generateSinglePdf(student, outputDir);
                generated.add(info);
                log.info("✅  PDF saved: {}", info.getFilePath());
            } catch (PdfGenerationException ex) {
                String msg = String.format("[%s] %s: %s",
                        student.getEnrollmentNo(), ex.getMessage(),
                        ex.getCause() != null ? ex.getCause().getMessage() : "");
                errors.add(msg);
                log.error("❌  {}", msg, ex);
            } catch (Exception ex) {
                String msg = String.format("[%s] Unexpected error: %s",
                        student.getEnrollmentNo(), ex.getMessage());
                errors.add(msg);
                log.error("❌  {}", msg, ex);
            }
        }

        int successCount = generated.size();
        int failureCount = errors.size();

        String status;
        String message;
        if (failureCount == 0) {
            status  = "SUCCESS";
            message = String.format("All %d PDF(s) generated successfully.", successCount);
        } else if (successCount > 0) {
            status  = "PARTIAL";
            message = String.format("%d PDF(s) generated; %d failed.", successCount, failureCount);
        } else {
            status  = "FAILURE";
            message = "No PDFs could be generated. See errors for details.";
        }

        log.info("Batch complete — status={}, success={}, failure={}",
                status, successCount, failureCount);

        return GenerateResponse.builder()
                .status(status)
                .message(message)
                .totalRequested(students.size())
                .successCount(successCount)
                .failureCount(failureCount)
                .outputDirectory(outputDir.toAbsolutePath().toString())
                .generatedFiles(generated)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ── Single-student PDF ────────────────────────────────────────────────────

    private GeneratedFileInfo generateSinglePdf(Student student, Path outputDir)
            throws PdfGenerationException {

        Path targetPath = fileNamingService.resolveOutputPath(
                outputDir, student.getEnrollmentNo(), student.getName());

        log.debug("Generating PDF for {} ({}) → {}",
                student.getName(), student.getEnrollmentNo(), targetPath);

        try (PDDocument document = new PDDocument()) {
            // Delegate all drawing to the layout service
            pdfLayoutService.buildStudentPdf(document, student);

            // Ensure parent directories exist (redundant safety guard)
            Files.createDirectories(targetPath.getParent());

            // Save document permanently to disk
            document.save(targetPath.toFile());

        } catch (IOException ex) {
            throw new PdfGenerationException(
                    student.getEnrollmentNo(),
                    "IO error while writing PDF to " + targetPath,
                    ex);
        } catch (Exception ex) {
            throw new PdfGenerationException(
                    student.getEnrollmentNo(),
                    "Unexpected error during PDF layout/save",
                    ex);
        }

        long fileSize;
        try {
            fileSize = Files.size(targetPath);
        } catch (IOException ex) {
            fileSize = -1L;
            log.warn("Could not read file size for {}: {}", targetPath, ex.getMessage());
        }

        return GeneratedFileInfo.builder()
                .studentName(student.getName())
                .enrollmentNo(student.getEnrollmentNo())
                .fileName(targetPath.getFileName().toString())
                .filePath(targetPath.toAbsolutePath().toString())
                .questionCount(student.getQuestions().size())
                .fileSizeBytes(fileSize)
                .build();
    }
}
