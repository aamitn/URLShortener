package com.bitmutex.shortener;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;



    // Generate and store OTP for a given username and phone number
    public String generateAndStoreOtp(String username, String phoneNumber) {
        // Generate a simple 4-digit OTP (you may use a more secure method)
        String generatedOtp = String.valueOf((int) (Math.random() * 9000) + 1000);

        // Store the OTP in the database
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setUsername(username);
        otpEntity.setPhoneNumber(phoneNumber);
        otpEntity.setOtp(generatedOtp);

        otpRepository.save(otpEntity);

        return generatedOtp;
    }

    public String generateAndStoreOtp(String email) {
        // Generate a simple 4-digit OTP (you may use a more secure method)
        String generatedOtp = String.valueOf((int) (Math.random() * 9000) + 1000);

        // Store the OTP in the database
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setUsername("NA");
        otpEntity.setPhoneNumber("NA");
        otpEntity.setEmail(email);
        otpEntity.setOtp(generatedOtp);

        otpRepository.save(otpEntity);

        return generatedOtp;
    }


    // Retrieve stored OTP for a given username and phone number
    public Optional<String> getOtp(String username, String phoneNumber) {
        OtpEntity otpEntity = otpRepository.findByUsernameAndPhoneNumber(username, phoneNumber);
        return Optional.ofNullable(otpEntity).map(OtpEntity::getOtp);
    }

    public Optional<String> getOtp(String email) {
        OtpEntity otpEntity = otpRepository.findByEmail(email);
        return Optional.ofNullable(otpEntity).map(OtpEntity::getOtp);
    }

    // Remove the stored OTP for a given username and phone number
    public void removeOtp(String username, String phoneNumber) {
        otpRepository.deleteById(username + phoneNumber);
    }


    // Remove the stored OTP for a given email
    public void removeOtpByEmail(String email) {
        OtpEntity otpEntity = otpRepository.findByEmail(email);

        if (otpEntity != null) {
            otpRepository.delete(otpEntity);
        } else {
            // Handle the case when no entry is found for the given email
            throw new NoSuchElementException("No entry found for email: " + email);
        }
    }

}