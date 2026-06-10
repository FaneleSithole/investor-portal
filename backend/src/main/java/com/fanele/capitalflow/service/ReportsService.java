package com.fanele.capitalflow.service;

import com.fanele.capitalflow.dto.ReportDto;
import com.fanele.capitalflow.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportsService {

    private static final List<ReportDto> REPORTS = List.of(
            new ReportDto("rpt_001", "Q4 2023 Consolidated Statement",
                    "Comprehensive overview of portfolio performance ending Dec 31, 2023.",
                    "STATEMENT", "2024-01-15", 2_516_582L),
            new ReportDto("rpt_002", "2023 Schedule K-1",
                    "Partner's share of income, deductions, credits, etc. for tax year 2023.",
                    "TAX_DOC", "2024-03-01", 1_153_434L),
            new ReportDto("rpt_003", "Annual Performance Review",
                    "Deep dive into sector allocations and yield comparisons.",
                    "ANALYSIS", "2024-01-20", 6_082_150L)
    );

    public List<ReportDto> listReports() {
        return REPORTS;
    }

    public ReportDto getReport(String id) {
        return REPORTS.stream()
                .filter(r -> r.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ApiException("Report not found", HttpStatus.NOT_FOUND));
    }
}
