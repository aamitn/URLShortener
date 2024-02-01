package com.bitmutex.shortener;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {

    // You can add custom query methods here if needed
}