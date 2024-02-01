package com.bitmutex.shortener;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // You can add custom queries or methods related to subscriptions here
    // Custom query to find a subscription by user
    Subscription findByUser(UserEntity user);
}