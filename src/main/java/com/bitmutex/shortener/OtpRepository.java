package com.bitmutex.shortener;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<OtpEntity, String> {
    // Custom query to find an OTP entry by username and phone number
     OtpEntity findByUsernameAndPhoneNumber(String username, String phoneNumber);
     OtpEntity findByEmail(String email);
     OtpEntity removeByEmail(String email);
}