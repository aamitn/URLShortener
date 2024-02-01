package com.bitmutex.shortener;

import jakarta.persistence.*;

//CONTAINS MAPPING WITH USER ID AND PLAN ID
@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public String getRazorpaySubscriptionId() {
        return razorpaySubscriptionId;
    }

    public void setRazorpaySubscriptionId(String razorpaySubscriptionId) {
        this.razorpaySubscriptionId = razorpaySubscriptionId;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan plan;

    @Column(name = "razorpay_subscription_id", nullable = true)
    private String razorpaySubscriptionId;

    // Getters and setters, constructors, etc.

    // Constructors, getters, setters, etc.
}