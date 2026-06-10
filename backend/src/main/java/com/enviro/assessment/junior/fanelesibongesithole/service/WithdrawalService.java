package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.dto.BalanceDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.TransactionDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.model.LinkedAccount;
import com.enviro.assessment.junior.fanelesibongesithole.model.Withdrawal;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalStatus;
import com.enviro.assessment.junior.fanelesibongesithole.model.WithdrawalType;
import com.enviro.assessment.junior.fanelesibongesithole.repository.DataStore;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class WithdrawalService {

    private static final int RETIREMENT_MIN_AGE = 65;
    private static final BigDecimal MAX_WITHDRAWAL_RATIO = new BigDecimal("0.90");

    private final DataStore dataStore;
    private final CurrentUserService currentUserService;

    public WithdrawalService(DataStore dataStore, CurrentUserService currentUserService) {
        this.dataStore = dataStore;
        this.currentUserService = currentUserService;
    }

    public BalanceDto getBalance() {
        BigDecimal available = calculateAvailableBalance();
        BigDecimal maxWithdrawal = available.multiply(MAX_WITHDRAWAL_RATIO)
                .setScale(2, RoundingMode.HALF_UP);
        boolean retirementEligible = currentUserService.requireCurrentUser().getAge() > RETIREMENT_MIN_AGE;

        return new BalanceDto(
                available.doubleValue(),
                maxWithdrawal.doubleValue(),
                2.4,
                retirementEligible
        );
    }

    public List<TransactionDto> getTransactions() {
        return dataStore.getWithdrawals().stream()
                .sorted(Comparator.comparing(Withdrawal::getDate).reversed())
                .map(this::toTransactionDto)
                .toList();
    }

    public WithdrawalResponseDto createWithdrawal(WithdrawalRequestDto request) {
        LinkedAccount account = dataStore.getAccounts().get(request.accountId());
        if (account == null) {
            throw new ApiException("Invalid destination account", HttpStatus.BAD_REQUEST);
        }

        validateWithdrawal(request);

        String referenceId = dataStore.nextReferenceId();
        Withdrawal withdrawal = new Withdrawal(
                referenceId,
                LocalDate.now(),
                request.amount().setScale(2, RoundingMode.HALF_UP),
                account.getBankName(),
                account.getLastFour(),
                WithdrawalStatus.Pending,
                request.type(),
                request.reason() != null ? request.reason() : ""
        );
        dataStore.addWithdrawal(withdrawal);

        return new WithdrawalResponseDto("Withdrawal request submitted", referenceId);
    }

    public String exportStatementsCsv(String status, String type, LocalDate from, LocalDate to) {
        Stream<Withdrawal> stream = dataStore.getWithdrawals().stream();

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

        List<Withdrawal> filtered = stream
                .sorted(Comparator.comparing(Withdrawal::getDate).reversed())
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("Reference ID,Date,Amount,Type,Status,Bank,Account Last Four,Reason\n");
        for (Withdrawal w : filtered) {
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

        if (request.type() == WithdrawalType.RETIREMENT && age <= RETIREMENT_MIN_AGE) {
            throw new ApiException(
                    "Retirement withdrawals are only available for investors over age 65",
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        BigDecimal available = calculateAvailableBalance();
        BigDecimal amount = request.amount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxAllowed = available.multiply(MAX_WITHDRAWAL_RATIO).setScale(2, RoundingMode.HALF_UP);

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
        BigDecimal pendingTotal = dataStore.getWithdrawals().stream()
                .filter(w -> w.getStatus() == WithdrawalStatus.Pending)
                .map(Withdrawal::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return dataStore.getLiquidBalance()
                .subtract(pendingTotal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private TransactionDto toTransactionDto(Withdrawal w) {
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
