package com.fanele.capitalflow.controller;

import com.fanele.capitalflow.dto.LinkedAccountDto;
import com.fanele.capitalflow.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/linked")
    public List<LinkedAccountDto> linked() {
        return accountService.getLinkedAccounts();
    }
}
