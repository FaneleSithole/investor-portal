package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.ComplianceSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class ComplianceService {

    public ComplianceSummaryDto getSummary() {
        return new ComplianceSummaryDto(
                94,
                124,
                3,
                1,
                true,
                "Private Equity Funds has exceeded the maximum single-issuer exposure limit of 15%. " +
                        "Current exposure is 16.2% in TechHoldings LLC."
        );
    }
}
