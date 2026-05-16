package com.telcoapihub.repository;

import com.telcoapihub.domain.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {
}
