package com.studentpdf.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Incoming request body from FastAPI.
 * Contains a list of students to generate PDFs for.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRequest {

    @NotEmpty(message = "Student list must not be empty")
    @Valid
    private List<Student> students;
}
