package com.studentpdf.service;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Handles safe, deterministic file-name generation for PDF files.
 *
 * <p>Rules:
 * <ul>
 *   <li>Filename: {@code <EnrollmentNo>_<SafeName>.pdf}</li>
 *   <li>Any character that is not alphanumeric, dash, or underscore is replaced with {@code _}</li>
 *   <li>If a file with the same name already exists, a timestamp suffix is appended to avoid collision</li>
 * </ul>
 */
@Service
public class FileNamingService {

    private static final Pattern UNSAFE = Pattern.compile("[^a-zA-Z0-9_\\-]");
    private static final ThreadLocal<SimpleDateFormat> TS_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd_HHmmssSSS"));

    /**
     * Build a safe filename (without directory).
     * E.g. "EN2024001_Aarav_Sharma.pdf"
     */
    public String buildFileName(String enrollmentNo, String name) {
        String safeEnroll = sanitize(enrollmentNo);
        String safeName   = sanitize(name);
        return safeEnroll + "_" + safeName + ".pdf";
    }

    /**
     * Resolve output path; if the file already exists, append a timestamp.
     */
    public Path resolveOutputPath(Path outputDir, String enrollmentNo, String name) {
        String base    = buildFileName(enrollmentNo, name);
        Path   target  = outputDir.resolve(base);

        if (target.toFile().exists()) {
            String ts  = TS_FORMAT.get().format(new Date());
            String safeEnroll = sanitize(enrollmentNo);
            String safeName   = sanitize(name);
            base   = safeEnroll + "_" + safeName + "_" + ts + ".pdf";
            target = outputDir.resolve(base);
        }

        return target;
    }

    private String sanitize(String input) {
        if (input == null || input.isBlank()) return "unknown";
        return UNSAFE.matcher(input.trim()).replaceAll("_");
    }
}
