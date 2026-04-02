package com.studentpdf.controller;

import com.studentpdf.model.GenerateRequest;
import com.studentpdf.model.GenerateResponse;
import com.studentpdf.service.PdfGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Primary REST controller — accepts student data from FastAPI
 * and triggers PDF generation.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PdfController {

    private final PdfGenerationService pdfGenerationService;

    /**
     * POST /api/generate
     *
     * <p>Accepts a JSON body of the form:
     * <pre>
     * {
     *   "students": [
     *     {
     *       "name": "Aarav Sharma",
     *       "enrollmentNo": "EN2024001",
     *       "questions": [ { "number": 1, "text": "...", "options": {...}, "answer": "A" }, ... ]
     *     }
     *   ]
     * }
     * </pre>
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerateResponse> generate(
            @Valid @RequestBody GenerateRequest request) {

        log.info("Received /api/generate request for {} student(s)",
                request.getStudents().size());

        GenerateResponse response = pdfGenerationService.generateAll(request);

        int httpStatus = switch (response.getStatus()) {
            case "SUCCESS" -> 200;
            case "PARTIAL" -> 207;  // Multi-Status
            default        -> 500;
        };

        return ResponseEntity.status(httpStatus).body(response);
    }
}
