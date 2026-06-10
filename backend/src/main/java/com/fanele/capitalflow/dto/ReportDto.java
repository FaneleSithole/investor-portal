package com.fanele.capitalflow.dto;

public record ReportDto(
        String id,
        String title,
        String description,
        String type,
        String date,
        long fileSizeBytes
) {}
