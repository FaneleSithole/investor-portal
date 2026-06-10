package com.fanele.capitalflow.service;

import com.fanele.capitalflow.dto.LinkedAccountDto;
import com.fanele.capitalflow.repository.DataStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final DataStore dataStore;

    public AccountService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<LinkedAccountDto> getLinkedAccounts() {
        return dataStore.getAccounts().values().stream()
                .map(a -> new LinkedAccountDto(a.getId(), a.getBankName(), a.getLastFour()))
                .toList();
    }
}
