package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.entity.ReferenceCounterEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.ReferenceCounterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReferenceCounterService {

    public static final String WITHDRAWAL_COUNTER = "withdrawal";
    public static final String INVESTMENT_COUNTER = "investment";

    private final ReferenceCounterRepository counterRepository;

    public ReferenceCounterService(ReferenceCounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    @Transactional
    public String nextWithdrawalReference() {
        int next = nextValue(WITHDRAWAL_COUNTER);
        return String.format("WDR-2024-%03d", next);
    }

    @Transactional
    public String nextInvestmentReference() {
        int next = nextValue(INVESTMENT_COUNTER);
        return String.format("INV-2024-%03d", next);
    }

    private int nextValue(String counterName) {
        ReferenceCounterEntity counter = counterRepository.findById(counterName)
                .orElseThrow(() -> new ApiException("Reference counter not configured: " + counterName,
                        HttpStatus.INTERNAL_SERVER_ERROR));
        int current = counter.getNextValue();
        counter.setNextValue(current + 1);
        counterRepository.save(counter);
        return current;
    }
}
