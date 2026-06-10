package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.ComplianceSummaryDto;
import com.fanele.capitalflow.service.ComplianceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping("/summary")
    public ComplianceSummaryDto summary() {
        return complianceService.getSummary();
    }
}
