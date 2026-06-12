package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioChartPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioChartPointRepository extends JpaRepository<PortfolioChartPointEntity, Long> {

    List<PortfolioChartPointEntity> findAllByOrderBySortOrderAsc();
}
