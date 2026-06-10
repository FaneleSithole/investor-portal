package com.fanele.capitalflow.service;

import com.fanele.capitalflow.catalog.FundCatalog;
import com.fanele.capitalflow.dto.InvestmentCommitmentRequest;
import com.fanele.capitalflow.dto.InvestmentCommitmentResponse;
import com.fanele.capitalflow.dto.InvestmentFundDto;
import com.fanele.capitalflow.entity.UserEntity;
import com.fanele.capitalflow.exception.ApiException;
import com.fanele.capitalflow.repository.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InvestmentService {

    private final FundCatalog fundCatalog;
    private final DataStore dataStore;
    private final CurrentUserService currentUserService;
    private final AtomicInteger commitmentCounter = new AtomicInteger(100);

    public InvestmentService(FundCatalog fundCatalog, DataStore dataStore,
                             CurrentUserService currentUserService) {
        this.fundCatalog = fundCatalog;
        this.dataStore = dataStore;
        this.currentUserService = currentUserService;
    }

    public List<InvestmentFundDto> listFunds() {
        return fundCatalog.all().stream().map(fundCatalog::toDto).toList();
    }

    public InvestmentFundDto getFund(String id) {
        return fundCatalog.find(id)
                .map(fundCatalog::toDto)
                .orElseThrow(() -> new ApiException("Fund not found", HttpStatus.NOT_FOUND));
    }

    public InvestmentCommitmentResponse createCommitment(InvestmentCommitmentRequest request) {
        UserEntity user = currentUserService.requireCurrentUser();
        FundCatalog.Fund fund = fundCatalog.find(request.fundId())
                .orElseThrow(() -> new ApiException("Fund not found", HttpStatus.NOT_FOUND));

        if (dataStore.getAccounts().get(request.accountId()) == null) {
            throw new ApiException("Invalid funding source account", HttpStatus.BAD_REQUEST);
        }

        if (!Boolean.TRUE.equals(request.accreditedInvestor())) {
            throw new ApiException("Accredited investor confirmation is required", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!Boolean.TRUE.equals(request.termsAccepted())) {
            throw new ApiException("Terms and conditions must be accepted", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String expectedName = user.getFullName();
        if (!expectedName.equalsIgnoreCase(request.digitalSignature().trim())) {
            throw new ApiException("Digital signature must match your registered full name", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        BigDecimal amount = request.amount();
        if (amount.compareTo(BigDecimal.valueOf(FundCatalog.MIN_COMMITMENT)) < 0) {
            throw new ApiException("Minimum commitment is R 4,500,000", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        dataStore.recordCommitment(fund.id(), amount, fund);

        String referenceId = String.format("INV-2024-%03d", commitmentCounter.incrementAndGet());
        return new InvestmentCommitmentResponse(
                "Investment commitment submitted for " + fund.name(),
                referenceId
        );
    }
}
