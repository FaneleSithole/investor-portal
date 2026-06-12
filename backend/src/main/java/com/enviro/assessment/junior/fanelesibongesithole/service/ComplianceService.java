package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.ComplianceSummaryDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.ComplianceSummaryEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ComplianceSummaryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ComplianceService {

    private final ComplianceSummaryRepository complianceSummaryRepository;

    public ComplianceService(ComplianceSummaryRepository complianceSummaryRepository) {
        this.complianceSummaryRepository = complianceSummaryRepository;
    }

    public ComplianceSummaryDto getSummary() {
        ComplianceSummaryEntity summary = complianceSummaryRepository.findById(1L)
                .orElseThrow(() -> new ApiException("Compliance summary not configured", HttpStatus.INTERNAL_SERVER_ERROR));

        return new ComplianceSummaryDto(
                summary.getPassRate(),
                summary.getRulesPassed(),
                summary.getRulesWarning(),
                summary.getRulesFailed(),
                summary.isActiveBreach(),
                summary.getBreachMessage()
        );
    }
}
