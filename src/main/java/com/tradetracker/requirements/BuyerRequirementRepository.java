package com.tradetracker.requirements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyerRequirementRepository extends JpaRepository<BuyerRequirement, Long> {
    List<BuyerRequirement> findAllByOrderByRequirementDateDesc();
}
