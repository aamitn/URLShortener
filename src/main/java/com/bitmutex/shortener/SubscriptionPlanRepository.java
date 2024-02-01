package com.bitmutex.shortener;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByPlanName(String planName);

    Optional<SubscriptionPlan> findSubscriptionPlanByPlanName(String planName);
}