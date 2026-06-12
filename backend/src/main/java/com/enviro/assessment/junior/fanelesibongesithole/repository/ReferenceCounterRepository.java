package com.enviro.assessment.junior.fanelesibongesithole.repository;

import com.enviro.assessment.junior.fanelesibongesithole.entity.ReferenceCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferenceCounterRepository extends JpaRepository<ReferenceCounterEntity, String> {}
