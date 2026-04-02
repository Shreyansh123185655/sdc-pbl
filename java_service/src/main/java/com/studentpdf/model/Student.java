package com.studentpdf.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Represents a single student with their exam questions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {

    @NotBlank(message = "Student name must not be blank")
    private String name;

    @NotBlank(message = "Enrollment number must not be blank")
    private String enrollmentNo;

    @NotEmpty(message = "Questions list must not be empty")
    @Size(min = 10, max = 20, message = "Each student must have between 10 and 20 questions")
    @Valid
    private List<Question> questions;
}
