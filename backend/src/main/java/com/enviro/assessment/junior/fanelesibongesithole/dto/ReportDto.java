package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record ReportDto(
        String id,
        String title,
        String description,
        String type,
        String date,
        long fileSizeBytes
) {}
