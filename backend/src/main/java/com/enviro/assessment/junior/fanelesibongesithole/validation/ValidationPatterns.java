package com.enviro.assessment.junior.fanelesibongesithole.validation;

public final class ValidationPatterns {

    public static final String FUND_ID = "fund_[a-z_]+";
    public static final String ACCOUNT_ID = "acc_\\d{3}";
    public static final String REPORT_ID = "rpt_\\d{3}";
    public static final String WITHDRAWAL_STATUS = "(?i)(Pending|Completed|Rejected)";
    public static final String WITHDRAWAL_TYPE = "(?i)(STANDARD|RETIREMENT)";
    public static final String PHONE = "^\\+?[0-9\\s\\-()]{7,30}$";
    public static final String OPTIONAL_PHONE = "^$|^\\+?[0-9\\s\\-()]{7,30}$";
    public static final String PERSON_NAME = "^[\\p{L}\\p{M}'\\-. ]{1,80}$";
    public static final String OPTIONAL_PERSON_NAME = "^$|^[\\p{L}\\p{M}'\\-. ]{1,80}$";

    private ValidationPatterns() {}
}
