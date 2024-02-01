package com.bitmutex.shortener;

import com.bitmutex.shortener.HomeController;
import com.bitmutex.shortener.UserDetailsDto;
import com.bitmutex.shortener.UserEntity;
import com.bitmutex.shortener.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class HomeControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testHomeAuthenticatedUser() {
        String username = "testUser";
        UserDetailsDto userDetails = new UserDetailsDto();
        userDetails.setUsername(username);
        userDetails.setEmail("test@example.com");
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(userService.getUserDetailsByUsername(username)).thenReturn(userDetails);
        when(userService.findByEmail(userDetails.getEmail())).thenReturn(userEntity);

        String viewName = homeController.home(authentication, model);

        assertEquals("index", viewName);
        verify(model).addAttribute("user", userDetails);
        verify(model).addAttribute("username", username);
        verify(model).addAttribute("userelevated", userEntity);
        verifyNoMoreInteractions(model); // Ensures that no other interactions with the model occurred
    }

}
