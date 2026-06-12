package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.entity.FundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FundRepository extends JpaRepository<FundEntity, String> {

    List<FundEntity> findAllByOrderBySortOrderAsc();
}
