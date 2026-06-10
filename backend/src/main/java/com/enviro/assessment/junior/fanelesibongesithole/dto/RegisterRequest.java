package com.enviro.assessment.junior.fanelesibongesithole.dto;

import com.enviro.assessment.junior.fanelesibongesithole.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 128, message = "Password must be at least 8 characters") String password,
        @NotBlank @Size(max = 80) @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Invalid first name")
        String firstName,
        @NotBlank @Size(max = 80) @Pattern(regexp = ValidationPatterns.PERSON_NAME, message = "Invalid last name")
        String lastName,
        @Size(max = 120) String firmName,
        @NotNull @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth
) {}
