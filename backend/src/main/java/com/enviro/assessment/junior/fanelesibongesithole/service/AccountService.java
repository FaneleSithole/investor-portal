package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.LinkedAccountDto;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final LinkedAccountRepository accountRepository;

    public AccountService(LinkedAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<LinkedAccountDto> getLinkedAccounts() {
        return accountRepository.findAll().stream()
                .map(a -> new LinkedAccountDto(a.getId(), a.getBankName(), a.getLastFour()))
                .toList();
    }
}
