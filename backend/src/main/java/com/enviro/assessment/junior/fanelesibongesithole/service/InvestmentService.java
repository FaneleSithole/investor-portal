package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.catalog.FundCatalog;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentCommitmentResponse;
import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentFundDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.FundEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.InvestmentCommitmentEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LinkedAccountEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioHoldingEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.FundRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.InvestmentCommitmentRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.PortfolioHoldingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvestmentService {

    private final FundCatalog fundCatalog;
    private final FundRepository fundRepository;
    private final LinkedAccountRepository accountRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final InvestmentCommitmentRepository commitmentRepository;
    private final ReferenceCounterService referenceCounterService;
    private final CurrentUserService currentUserService;

    public InvestmentService(FundCatalog fundCatalog,
                             FundRepository fundRepository,
                             LinkedAccountRepository accountRepository,
                             PortfolioHoldingRepository holdingRepository,
                             InvestmentCommitmentRepository commitmentRepository,
                             ReferenceCounterService referenceCounterService,
                             CurrentUserService currentUserService) {
        this.fundCatalog = fundCatalog;
        this.fundRepository = fundRepository;
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.commitmentRepository = commitmentRepository;
        this.referenceCounterService = referenceCounterService;
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

    @Transactional
    public InvestmentCommitmentResponse createCommitment(InvestmentCommitmentRequest request) {
        UserEntity user = currentUserService.requireCurrentUser();
        FundCatalog.Fund fund = fundCatalog.find(request.fundId())
                .orElseThrow(() -> new ApiException("Fund not found", HttpStatus.NOT_FOUND));

        LinkedAccountEntity account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ApiException("Invalid funding source account", HttpStatus.BAD_REQUEST));

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
        if (amount.compareTo(BigDecimal.valueOf(fund.minCommitment())) < 0) {
            throw new ApiException("Minimum commitment is R 4,500,000", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        FundEntity fundEntity = fundRepository.findById(fund.id())
                .orElseThrow(() -> new ApiException("Fund not found", HttpStatus.NOT_FOUND));
        recordCommitment(fundEntity, amount);

        String referenceId = referenceCounterService.nextInvestmentReference();
        commitmentRepository.save(new InvestmentCommitmentEntity(
                referenceId,
                fundEntity,
                account,
                user,
                amount,
                request.fundingDate()
        ));

        return new InvestmentCommitmentResponse(
                "Investment commitment submitted for " + fund.name(),
                referenceId
        );
    }

    private void recordCommitment(FundEntity fund, BigDecimal amount) {
        holdingRepository.findByFund_Id(fund.getId()).ifPresentOrElse(
                holding -> {
                    holding.setCommitted(holding.getCommitted().add(amount));
                    holding.setCurrentValue(holding.getCurrentValue().add(amount));
                    holdingRepository.save(holding);
                },
                () -> holdingRepository.save(new PortfolioHoldingEntity(
                        fund,
                        amount,
                        BigDecimal.ZERO,
                        amount,
                        0.0
                ))
        );
    }
}
