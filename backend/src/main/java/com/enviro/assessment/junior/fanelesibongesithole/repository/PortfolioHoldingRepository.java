package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioHoldingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHoldingEntity, Long> {

    Optional<PortfolioHoldingEntity> findByFund_Id(String fundId);
}
