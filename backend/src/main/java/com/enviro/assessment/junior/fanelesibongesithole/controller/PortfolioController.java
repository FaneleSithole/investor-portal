package com.enviro.assessment.junior.fanelesibongesithole.controller;

import com.enviro.assessment.junior.fanelesibongesithole.dto.PortfolioSummaryDto;
import com.enviro.assessment.junior.fanelesibongesithole.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/summary")
    public PortfolioSummaryDto summary() {
        return portfolioService.getSummary();
    }
}
