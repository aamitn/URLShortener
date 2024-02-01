package com.bitmutex.shortener;

import jakarta.persistence.*;

import java.math.BigDecimal;

// CONTAINS DETAILS OF EACH SUBSCRIPTION PLAN
@Entity
@Table(name = "subscription_plan")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(name = "plan_name", unique = true, nullable = false)
    private String planName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getRazorpayPlanId() {
        return razorpayPlanId;
    }

    public void setRazorpayPlanId(String razorpayPlanId) {
        this.razorpayPlanId = razorpayPlanId;
    }

    public int getMaxShortUrls() {
        return maxShortUrls;
    }

    public void setMaxShortUrls(int maxShortUrls) {
        this.maxShortUrls = maxShortUrls;
    }

    public int getMaxBioPages() {
        return maxBioPages;
    }

    public void setMaxBioPages(int maxBioPages) {
        this.maxBioPages = maxBioPages;
    }

    @Column(name = "razorpay_plan_id", nullable = false)
    private String razorpayPlanId;

    @Column(name = "max_short_urls", nullable = false)
    private int maxShortUrls;

    @Column(name = "max_bio_pages", nullable = false)
    private int maxBioPages;


    // Check if the plan is free
    public boolean isFree() {
        return "Free".equalsIgnoreCase(planName);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    private BigDecimal price; // New field for the plan price

}