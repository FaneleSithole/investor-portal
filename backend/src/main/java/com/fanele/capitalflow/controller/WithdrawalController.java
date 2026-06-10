package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.BalanceDto;
import com.fanele.capitalflow.dto.TransactionDto;
import com.fanele.capitalflow.dto.WithdrawalExportQuery;
import com.fanele.capitalflow.dto.WithdrawalRequestDto;
import com.fanele.capitalflow.dto.WithdrawalResponseDto;
import com.fanele.capitalflow.service.WithdrawalService;
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

    @PostMapping("/request")
    public WithdrawalResponseDto request(@Valid @RequestBody WithdrawalRequestDto body) {
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
