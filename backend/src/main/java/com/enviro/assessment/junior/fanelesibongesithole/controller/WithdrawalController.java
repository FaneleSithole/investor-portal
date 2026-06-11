package com.enviro.assessment.junior.fanelesibongesithole.controller;

import com.enviro.assessment.junior.fanelesibongesithole.dto.BalanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.TransactionDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalExportQuery;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/balance")
    public BalanceDto balance() {
        return withdrawalService.getBalance();
    }

    @GetMapping("/transactions")
    public List<TransactionDto> transactions() {
        return withdrawalService.getTransactions();
    }

    @PostMapping
    public WithdrawalResponseDto create(@Valid @RequestBody WithdrawalRequestDto body) {
        return withdrawalService.createWithdrawal(body);
    }

    @GetMapping("/statements/export")
    public ResponseEntity<String> exportStatements(@Valid @ModelAttribute WithdrawalExportQuery query) {
        String csv = withdrawalService.exportStatementsCsv(
                query.status(), query.type(), query.from(), query.to());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"withdrawal-statements.csv\"")
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }
}
