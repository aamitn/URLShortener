package com.bitmutex.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    public void sendSms(String toNumber, String message) {
        try {
            // Send the SMS to the user (you need to implement this part)
            var client = HttpClient.newHttpClient();
            var apiKey = "Vucf1nCa4ed-AMNGv6CnsycfQT28yLUA8NEvY7IZ87-Piv855UBcjfo29Zb8XPZt";

            String payload = "{\n" +
                    "  \"content\": \"" + message + "\",\n" +  // Use concatenation for variables
                    "  \"from\": \"+9038556097\",\n" +
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
            logger.info("SMS sent successfully to {}", toNumber);
            logger.debug("SMS API response: {}", response.body());
        } catch (Exception e) {
            // Log and handle SMS sending failure
            logger.error("Failed to send SMS to {}: {}", toNumber, e.getMessage(), e);
        }
    }
}
