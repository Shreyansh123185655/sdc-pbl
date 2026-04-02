package com.studentpdf.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reads the pdf.output.dir property, resolves it to an absolute path,
 * and ensures the directory exists on startup.
 */
@Configuration
@Getter
@Slf4j
public class PdfOutputConfig {

    @Value("${pdf.output.dir:./output}")
    private String rawOutputDir;

    private Path outputPath;

    @PostConstruct
    public void init() throws IOException {
        outputPath = Paths.get(rawOutputDir).toAbsolutePath().normalize();
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
            log.info("PDF output directory created: {}", outputPath);
        } else {
            log.info("PDF output directory exists: {}", outputPath);
        }
    }
}
