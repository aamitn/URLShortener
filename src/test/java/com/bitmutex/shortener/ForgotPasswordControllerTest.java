package com.bitmutex.shortener;

import com.bitmutex.shortener.ForgotPasswordController;
import com.bitmutex.shortener.UserEntity;
import com.bitmutex.shortener.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ForgotPasswordControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @BeforeMethod
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShowForgotPasswordForm() {
        String viewName = forgotPasswordController.showForgotPasswordForm();
        assert "forgot-password".equals(viewName);
    }

    @Test
    public void testProcessForgotPasswordUserNotFound() {
        String email = "nonexistent@example.com";

        when(userService.findByEmail(email)).thenReturn(null);

        String result = forgotPasswordController.processForgotPassword(email, model, request);

        assert "forgot-password".equals(result);

        verify(userService, times(1)).findByEmail(email);
        verify(userService, never()).save(any(UserEntity.class));
        verify(javaMailSender, never()).send((MimeMessage) any());
    }
}
