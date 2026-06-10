package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.ReportDto;
import com.fanele.capitalflow.service.ReportsService;
import com.fanele.capitalflow.validation.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping
    public List<ReportDto> list() {
        return reportsService.listReports();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable @NotBlank @Pattern(regexp = ValidationPatterns.REPORT_ID, message = "Invalid report ID")
            String id) {
        reportsService.getReport(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new byte[0]);
    }

    @GetMapping("/download-all")
    public ResponseEntity<byte[]> downloadAll() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"all-reports.zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(new byte[0]);
    }
}
