package com.enviro.assessment.junior.fanelesibongesithole.validation;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationPatternsTest {

    @Test
    void fundIdPattern_matchesCatalogIds() {
        Pattern pattern = Pattern.compile(ValidationPatterns.FUND_ID);
        assertTrue(pattern.matcher("fund_pe").matches());
        assertTrue(pattern.matcher("fund_balanced").matches());
        assertFalse(pattern.matcher("FUND_PE").matches());
        assertFalse(pattern.matcher("fund-123").matches());
    }

    @Test
    void accountIdPattern_matchesLinkedAccounts() {
        Pattern pattern = Pattern.compile(ValidationPatterns.ACCOUNT_ID);
        assertTrue(pattern.matcher("acc_001").matches());
        assertFalse(pattern.matcher("acc_01").matches());
        assertFalse(pattern.matcher("account_001").matches());
    }

    @Test
    void personNamePattern_acceptsUnicodeNames() {
        Pattern pattern = Pattern.compile(ValidationPatterns.PERSON_NAME);
        assertTrue(pattern.matcher("Thabo").matches());
        assertTrue(pattern.matcher("O'Brien").matches());
        assertFalse(pattern.matcher("Thabo123").matches());
    }

    @Test
    void phonePattern_acceptsInternationalFormats() {
        Pattern pattern = Pattern.compile(ValidationPatterns.PHONE);
        assertTrue(pattern.matcher("+27 82 555 1234").matches());
        assertTrue(pattern.matcher("(555) 123-4567").matches());
        assertFalse(pattern.matcher("abc").matches());
    }
}
