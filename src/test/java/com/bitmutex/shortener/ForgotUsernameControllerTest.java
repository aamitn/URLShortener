package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class ForgotUsernameControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private ForgotUsernameController forgotUsernameController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Model model;

    @BeforeMethod
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShowForgotUsernameForm() {
        String viewName = forgotUsernameController.showForgotUsernameForm();
        assert "forgot-username".equals(viewName);
    }

    @Test
    public void testProcessForgotUsernameUserNotFound() {
        String email = "nonexistent@example.com";

        when(userService.findByEmail(email)).thenReturn(null);

        String result = forgotUsernameController.processForgotUsername(email, model, request);

        assertEquals("forgot-username", result);

        verify(userService, times(1)).findByEmail(email);
        verify(javaMailSender, never()).send((SimpleMailMessage) any());
    }


}
