package com.enviro.assessment.junior.fanelesibongesithole.dto;

public record ProductDto(
        String id,
        String name,
        String assetClass,
        long committed,
        long invested,
        long currentValue,
        double irr
) {}
