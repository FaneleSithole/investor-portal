package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.domain.BusinessRules;
import com.enviro.assessment.junior.fanelesibongesithole.dto.BalanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.TransactionDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LinkedAccountEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.LiquidBalanceEntity;
import com.enviro.assessment.junior.fanelesibongesithole.entity.WithdrawalEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LinkedAccountRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.LiquidBalanceRepository;
import com.enviro.assessment.junior.fanelesibongesithole.repository.WithdrawalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final LinkedAccountRepository accountRepository;
    private final LiquidBalanceRepository liquidBalanceRepository;
    private final ReferenceCounterService referenceCounterService;
    private final CurrentUserService currentUserService;

    public WithdrawalService(WithdrawalRepository withdrawalRepository,
                             LinkedAccountRepository accountRepository,
                             LiquidBalanceRepository liquidBalanceRepository,
                             ReferenceCounterService referenceCounterService,
                             CurrentUserService currentUserService) {
        this.withdrawalRepository = withdrawalRepository;
        this.accountRepository = accountRepository;
        this.liquidBalanceRepository = liquidBalanceRepository;
        this.referenceCounterService = referenceCounterService;
        this.currentUserService = currentUserService;
    }

    public BalanceDto getBalance() {
        BigDecimal available = calculateAvailableBalance();
        BigDecimal maxWithdrawal = available.multiply(BusinessRules.MAX_WITHDRAWAL_RATIO)
                .setScale(2, RoundingMode.HALF_UP);
        boolean retirementEligible = currentUserService.requireCurrentUser().getAge() > BusinessRules.RETIREMENT_MIN_AGE;
        double growthPercent = liquidBalanceRepository.findById(1L)
                .map(LiquidBalanceEntity::getGrowthPercent)
                .orElseThrow(() -> new ApiException("Liquid balance not configured", HttpStatus.INTERNAL_SERVER_ERROR));

        return new BalanceDto(
                available.doubleValue(),
                maxWithdrawal.doubleValue(),
                growthPercent,
                retirementEligible
        );
    }

    public List<TransactionDto> getTransactions() {
        return withdrawalRepository.findAll().stream()
                .sorted(Comparator.comparing(WithdrawalEntity::getDate).reversed())
                .map(this::toTransactionDto)
                .toList();
    }

    @Transactional
    public WithdrawalResponseDto createWithdrawal(WithdrawalRequestDto request) {
        LinkedAccountEntity account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new ApiException("Invalid destination account", HttpStatus.BAD_REQUEST));

        validateWithdrawal(request);

        String referenceId = referenceCounterService.nextWithdrawalReference();
        WithdrawalEntity withdrawal = new WithdrawalEntity(
                referenceId,
                LocalDate.now(),
                request.amount().setScale(2, RoundingMode.HALF_UP),
                account.getBankName(),
                account.getLastFour(),
                WithdrawalStatus.Pending,
                request.type(),
                request.reason() != null ? request.reason() : ""
        );
        withdrawalRepository.save(withdrawal);

        return new WithdrawalResponseDto("Withdrawal request submitted", referenceId);
    }

    public String exportStatementsCsv(String status, String type, LocalDate from, LocalDate to) {
        Stream<WithdrawalEntity> stream = withdrawalRepository.findAll().stream();

        if (status != null && !status.isBlank()) {
            WithdrawalStatus filterStatus = parseStatus(status);
            stream = stream.filter(w -> w.getStatus() == filterStatus);
        }
        if (type != null && !type.isBlank()) {
            WithdrawalType filterType = WithdrawalType.valueOf(type.toUpperCase());
            stream = stream.filter(w -> w.getType() == filterType);
        }
        if (from != null) {
            stream = stream.filter(w -> !w.getDate().isBefore(from));
        }
        if (to != null) {
            stream = stream.filter(w -> !w.getDate().isAfter(to));
        }

        List<WithdrawalEntity> filtered = stream
                .sorted(Comparator.comparing(WithdrawalEntity::getDate).reversed())
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("Reference ID,Date,Amount,Type,Status,Bank,Account Last Four,Reason\n");
        for (WithdrawalEntity w : filtered) {
            csv.append(w.getReferenceId()).append(',')
                    .append(w.getDate()).append(',')
                    .append(w.getAmount()).append(',')
                    .append(w.getType()).append(',')
                    .append(w.getStatus()).append(',')
                    .append(escapeCsv(w.getBankName())).append(',')
                    .append(w.getLastFour()).append(',')
                    .append(escapeCsv(w.getReason())).append('\n');
        }
        return csv.toString();
    }

    private void validateWithdrawal(WithdrawalRequestDto request) {
        int age = currentUserService.requireCurrentUser().getAge();

        if (request.type() == WithdrawalType.RETIREMENT && age <= BusinessRules.RETIREMENT_MIN_AGE) {
            throw new ApiException(
                    "Retirement withdrawals are only available for investors over age 65",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        BigDecimal available = calculateAvailableBalance();
        BigDecimal amount = request.amount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxAllowed = available.multiply(BusinessRules.MAX_WITHDRAWAL_RATIO).setScale(2, RoundingMode.HALF_UP);

        if (amount.compareTo(available) > 0) {
            throw new ApiException(
                    "Amount exceeds available balance of " + available,
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (amount.compareTo(maxAllowed) > 0) {
            throw new ApiException(
                    "Amount exceeds the 90% withdrawal limit of " + maxAllowed,
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private BigDecimal calculateAvailableBalance() {
        BigDecimal pendingTotal = withdrawalRepository.findAll().stream()
                .filter(w -> w.getStatus() == WithdrawalStatus.Pending)
                .map(WithdrawalEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal liquidBalance = liquidBalanceRepository.findById(1L)
                .map(LiquidBalanceEntity::getBalance)
                .orElseThrow(() -> new ApiException("Liquid balance not configured", HttpStatus.INTERNAL_SERVER_ERROR));

        return liquidBalance
                .subtract(pendingTotal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private TransactionDto toTransactionDto(WithdrawalEntity w) {
        return new TransactionDto(
                w.getReferenceId(),
                w.getDate().toString(),
                w.getAmount().doubleValue(),
                w.getBankName(),
                w.getLastFour(),
                w.getStatus().name(),
                w.getType().name(),
                w.getReason()
        );
    }

    private WithdrawalStatus parseStatus(String status) {
        for (WithdrawalStatus value : WithdrawalStatus.values()) {
            if (value.name().equalsIgnoreCase(status)) {
                return value;
            }
        }
        throw new ApiException("Invalid status filter: " + status, HttpStatus.BAD_REQUEST);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
