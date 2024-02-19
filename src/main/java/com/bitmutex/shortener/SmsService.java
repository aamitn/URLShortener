package com.bitmutex.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class SmsService {

    private final SmsServiceConfig smsServiceConfig;

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public SmsService(SmsServiceConfig smsServiceConfig) {
        this.smsServiceConfig = smsServiceConfig;
    }

    public void sendSms(String toNumber, String message) {
        try {
            if ("managed".equals(smsServiceConfig.getSmsProvider())) {
                sendSmsToManagedProvider(toNumber, message);
            } else if ("selfhosted".equals(smsServiceConfig.getSmsProvider())) {
                sendSmsToSelfHostedGateway(toNumber, message);
            } else {
                logger.error("Invalid SMS provider configuration");
            }
            logger.info("SMS sent successfully to {}", toNumber);
        } catch (Exception e) {
            // Log and handle SMS sending failure
            logger.error("Failed to send SMS to {}: {}", toNumber, e.getMessage(), e);
        }
    }

    private void sendSmsToManagedProvider(String toNumber, String message) throws Exception {
        var client = HttpClient.newHttpClient();

        var apiKey = smsServiceConfig.getManagedSmsApiKey();
        var phoneNumber = smsServiceConfig.getManagedPhoneNumber();

        String payload = "{\n" +
                "  \"content\": \"" + message + "\",\n" +
                "  \"from\": \"+"+ phoneNumber + "\",\n" +
                "  \"to\": \"" + toNumber + "\"\n" +
                "}";

        var requestBuild = HttpRequest.newBuilder()
                .uri(URI.create("https://api.httpsms.com/v1/messages/send"))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        var response = client.send(requestBuild, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        logger.info("External SMS API response: {}", response.body());
    }

    private void sendSmsToSelfHostedGateway(String toNumber, String message) throws Exception {
        // Replace with your actual SMSGateway server URL, device ID, and hash

        String gatewayUrl = smsServiceConfig.getSelfHostedGatewayUrl();
        String deviceId = smsServiceConfig.getSelfHostedDeviceId();
        String hash = smsServiceConfig.getSelfHostedHash();

        // Encode special characters in the message
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());

        // Build the final URL
        var finalUrl = String.format("%s?id=%s&h=%s&to=%s&message=%s", gatewayUrl, deviceId, hash, toNumber, encodedMessage);

        var client = HttpClient.newHttpClient();

        var requestBuild = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        var response = client.send(requestBuild, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        logger.info("SMSGateway API response: {}", response.body());
    }
}