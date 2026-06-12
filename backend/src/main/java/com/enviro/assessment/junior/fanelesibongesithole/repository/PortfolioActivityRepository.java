package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.entity.PortfolioActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioActivityRepository extends JpaRepository<PortfolioActivityEntity, Long> {

    List<PortfolioActivityEntity> findAllByOrderBySortOrderAsc();
}
