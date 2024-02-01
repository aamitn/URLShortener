package com.bitmutex.shortener;

import com.bitmutex.shortener.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private SubscriptionPaymentRepository subscriptionPaymentRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSubscription_Success() {
        // Mocking dependencies
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        subscriptionPlan.setPlanName("Basic");
        when(subscriptionPlanRepository.findSubscriptionPlanByPlanName(anyString())).thenReturn(java.util.Optional.of(subscriptionPlan));

        // Test
        subscriptionService.createSubscription(userEntity, "Basic");

        // Verify that the save method was called on subscriptionRepository
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void createSubscription_PlanNotFound() {
        // Mocking dependencies
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        when(subscriptionPlanRepository.findSubscriptionPlanByPlanName(anyString())).thenReturn(java.util.Optional.empty());

        // Test and assert that SubscriptionPlanNotFoundException is thrown
        assertThrows(SubscriptionPlanNotFoundException.class, () ->
                subscriptionService.createSubscription(userEntity, "NonExistingPlan")
        );

        // Verify that save method on subscriptionRepository is not called
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void changeSubscription_Success() {
        // Mocking dependencies
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");

        Subscription currentSubscription = new Subscription();
        SubscriptionPlan currentSubscriptionPlan = new SubscriptionPlan();
        currentSubscriptionPlan.setPlanName("Basic");
        currentSubscription.setPlan(currentSubscriptionPlan);
        when(subscriptionRepository.findByUser(userEntity)).thenReturn(currentSubscription);

        SubscriptionPlan newSubscriptionPlan = new SubscriptionPlan();
        newSubscriptionPlan.setPlanName("Premium");
        when(subscriptionPlanRepository.findByPlanName(anyString())).thenReturn(java.util.Optional.of(newSubscriptionPlan));

        // Test
        subscriptionService.changeSubscription(userEntity, "Premium");

        // Verify that save method was called on subscriptionRepository
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));

        // Verify that save method was called on subscriptionPaymentRepository
        verify(subscriptionPaymentRepository, times(1)).save(any(SubscriptionPayment.class));
    }
}
