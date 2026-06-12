package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.ReportDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.ReportEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportsService {

    private final ReportRepository reportRepository;

    public ReportsService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<ReportDto> listReports() {
        return reportRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ReportDto getReport(String id) {
        return reportRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ApiException("Report not found", HttpStatus.NOT_FOUND));
    }

    private ReportDto toDto(ReportEntity entity) {
        return new ReportDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType(),
                entity.getGeneratedDate().toString(),
                entity.getFileSizeBytes()
        );
    }
}
