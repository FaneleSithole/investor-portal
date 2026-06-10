package com.enviro.assessment.junior.fanelesibongesithole.controller;

import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentResponse;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentFundDto;
import com.enviro.assessment.junior.fanelesibongesithole.service.InvestmentService;
import com.enviro.assessment.junior.fanelesibongesithole.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping("/funds")
    public List<InvestmentFundDto> listFunds() {
        return investmentService.listFunds();
    }

    @GetMapping("/funds/{id}")
    public InvestmentFundDto getFund(
            @PathVariable @NotBlank @Pattern(regexp = ValidationPatterns.FUND_ID, message = "Invalid fund ID")
            String id) {
        return investmentService.getFund(id);
    }

    @PostMapping("/commitments")
    public ResponseEntity<InvestmentCommitmentResponse> createCommitment(
            @Valid @RequestBody InvestmentCommitmentRequest request) {
        InvestmentCommitmentResponse response = investmentService.createCommitment(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/api/investments/commitments/" + response.referenceId()))
                .body(response);
    }
}
