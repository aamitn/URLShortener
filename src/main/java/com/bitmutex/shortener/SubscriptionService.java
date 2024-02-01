package com.bitmutex.shortener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final SubscriptionPaymentRepository subscriptionPaymentRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionPlanRepository subscriptionPlanRepository,SubscriptionPaymentRepository subscriptionPaymentRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPaymentRepository = subscriptionPaymentRepository;
    }

    public void createSubscription(UserEntity userEntity, String planName) {
        // Retrieve the subscription plan
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findSubscriptionPlanByPlanName(planName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found: " + planName));

        try{
            // Create a new subscription
            Subscription subscription = new Subscription();
            subscription.setUser(userEntity);
            subscription.setPlan(subscriptionPlan);

            // Save the subscription to the database
            subscriptionRepository.save(subscription);

            log.info("SUCCESS: Plan with name:"+subscriptionPlan.getPlanName()+"set for user with username:"+userEntity.getUsername());
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }

    }

    public void changeSubscription(UserEntity userEntity, String newPlanName) {
        // Retrieve the current subscription of the user
        Subscription currentSubscription = subscriptionRepository.findByUser(userEntity);

        // Retrieve the new subscription plan
        SubscriptionPlan newSubscriptionPlan = subscriptionPlanRepository.findByPlanName(newPlanName)
                .orElseThrow(() -> new SubscriptionPlanNotFoundException("Subscription plan not found: " + newPlanName));

        // Check if the new plan is the same as the current plan
        if (currentSubscription.getPlan().equals(newSubscriptionPlan)) {
            // No need to change the subscription, as the user is already on the desired plan
            return;
        }


            // Save the new subscription and update the user's subscription plan
            currentSubscription.setPlan(newSubscriptionPlan);
           // currentSubscription.setRazorpaySubscriptionId();
            subscriptionRepository.save(currentSubscription);

            // Record the payment in the existing subscription_payment table
            SubscriptionPayment subscriptionPayment = new SubscriptionPayment();
            subscriptionPayment.setSubscription(currentSubscription);
            subscriptionPayment.setAmount(newSubscriptionPlan.getPrice());
            subscriptionPayment.setPaymentStatus("SUCCESS"); // Set payment status based on actual payment verification
            subscriptionPaymentRepository.save(subscriptionPayment);

    }

    public Map<String, Object> getCurrentSubscriptionDetails(UserEntity userEntity) {
        // Retrieve the current subscription of the user
        Subscription currentSubscription = subscriptionRepository.findByUser(userEntity);

        // Retrieve additional details such as subscription plan name, max short URL, max bio pages, etc.
        SubscriptionPlan currentSubscriptionPlan = currentSubscription.getPlan();

        // Create a map to store the subscription details
        Map<String, Object> subscriptionDetails = new HashMap<>();
        subscriptionDetails.put("planName", currentSubscriptionPlan.getPlanName());
        subscriptionDetails.put("maxShortUrl", currentSubscriptionPlan.getMaxShortUrls());
        subscriptionDetails.put("maxBioPages", currentSubscriptionPlan.getMaxBioPages());
        // Add more fields as needed

        return subscriptionDetails;
    }
}