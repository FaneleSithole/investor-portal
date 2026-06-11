package com.enviro.assessment.junior.fanelesibongesithole.domain;

import java.math.BigDecimal;

/**
 * Shared business rules for withdrawal validation (assessment Section 02).
 */
public final class BusinessRules {

    public static final int RETIREMENT_MIN_AGE = 65;
    public static final BigDecimal MAX_WITHDRAWAL_RATIO = new BigDecimal("0.90");

    private BusinessRules() {}
}
