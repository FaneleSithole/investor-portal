package com.enviro.assessment.junior.fanelesibongesithole.catalog;

import com.enviro.assessment.junior.fanelesibongesithole.dto.InvestmentFundDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.FundEntity;
import com.enviro.assessment.junior.fanelesibongesithole.repository.FundRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Database-backed catalogue for investable funds — shared by portfolio holdings and new investments.
 */
@Component
public class FundCatalog {

    public static final long MIN_COMMITMENT = 4_500_000L;

    public record Fund(
            String id,
            String name,
            String description,
            String horizon,
            String assetClass,
            long minCommitment
    ) {}

    private final FundRepository fundRepository;

    public FundCatalog(FundRepository fundRepository) {
        this.fundRepository = fundRepository;
    }

    public List<Fund> all() {
        return fundRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toFund)
                .toList();
    }

    public Optional<Fund> find(String id) {
        return fundRepository.findById(id).map(this::toFund);
    }

    public Fund require(String id) {
        return fundRepository.findById(id)
                .map(this::toFund)
                .orElseThrow(() -> new IllegalArgumentException("Unknown fund: " + id));
    }

    public InvestmentFundDto toDto(Fund fund) {
        return new InvestmentFundDto(
                fund.id(),
                fund.name(),
                fund.description(),
                fund.horizon(),
                fund.minCommitment()
        );
    }

    private Fund toFund(FundEntity entity) {
        return new Fund(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getHorizon(),
                entity.getAssetClass(),
                entity.getMinCommitment()
        );
    }
}
