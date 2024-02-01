package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VerificationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private Model model;

    @InjectMocks
    private VerificationController verificationController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowVerificationPage() {
        // Arrange
        String email = "test@example.com";

        // Act
        String viewName = verificationController.showVerificationPage(email, model);

        // Assert
        assert viewName.equals("verify-registration");
        verify(model).addAttribute("email", email);
    }

    @Test
    public void testVerifyRegistration_Success() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";

        when(model.getAttribute("email")).thenReturn(email);
        when(otpService.getOtp(email)).thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(email)).thenReturn(new UserEntity());

        // Act
        ResponseEntity<String> responseEntity = verificationController.verifyRegistration(otp, email, model);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().contains("User verified successfully");
        verify(otpService).removeOtpByEmail(email);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    public void testVerifyRegistration_WrongOtp() {
        // Arrange
        String email = "test@example.com";
        String enteredOtp = "654321";
        String storedOtp = "123456";

        when(model.getAttribute("email")).thenReturn(email);
        when(otpService.getOtp(email)).thenReturn(Optional.of(storedOtp));

        // Act
        ResponseEntity<String> responseEntity = verificationController.verifyRegistration(enteredOtp, email, model);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert responseEntity.getBody().contains("you entered the wrong OTP");
    }
}
