package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsServiceConfig {

    @Value("${sms.provider}")
    private String smsProvider;

    @Value("${managed.sms.api.key}")
    private String managedSmsApiKey;

    @Value("${managed.sms.phone-number}")
    private String managedPhoneNumber;

    @Value("${selfhosted.gateway.url}")
    private String selfHostedGatewayUrl;

    @Value("${selfhosted.device.id}")
    private String selfHostedDeviceId;

    @Value("${selfhosted.hash}")
    private String selfHostedHash;

    public String getSmsProvider() {
        return smsProvider;
    }

    public String getManagedPhoneNumber() {
        return managedPhoneNumber;
    }

    public String getManagedSmsApiKey() {
        return managedSmsApiKey;
    }

    public String getSelfHostedGatewayUrl() {
        return selfHostedGatewayUrl;
    }

    public String getSelfHostedDeviceId() {
        return selfHostedDeviceId;
    }

    public String getSelfHostedHash() {
        return selfHostedHash;
    }
}
