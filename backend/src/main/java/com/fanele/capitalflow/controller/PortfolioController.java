package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.PortfolioSummaryDto;
import com.fanele.capitalflow.service.PortfolioService;
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
