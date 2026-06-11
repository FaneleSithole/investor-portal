package com.enviro.assessment.junior.fanelesibongesithole.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ValidationTestSupport {

    static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationTestSupport() {}

    static void assertValid(Object dto) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.isEmpty(), () -> formatViolations(violations));
    }

    static void assertFieldInvalid(Object dto, String field, String messageFragment) {
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "Expected validation failure for " + field);
        boolean matched = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals(field)
                        && v.getMessage().contains(messageFragment));
        assertTrue(matched, () -> "Expected " + field + " to contain '" + messageFragment
                + "' but got: " + formatViolations(violations));
    }

    private static String formatViolations(Set<ConstraintViolation<Object>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
    }
}
