package com.studentpdf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * Represents a single exam question with multiple-choice options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    /** 1-based question number */
    @NotNull(message = "Question number must not be null")
    private Integer number;

    /** The question text */
    @NotBlank(message = "Question text must not be blank")
    private String text;

    /**
     * Map of option key → option text.
     * Keys are typically "A", "B", "C", "D".
     */
    @NotNull(message = "Options map must not be null")
    private Map<String, String> options;

    /**
     * Correct answer key (e.g. "A", "B", "C", "D").
     * Optional — PDF will still be generated if missing.
     */
    private String answer;
}
