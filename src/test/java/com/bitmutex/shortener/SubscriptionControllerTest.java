package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testChangeSubscription_Success() {
        // Arrange
        String username = "testUser";
        String newPlanName = "newPlan";

        UserEntity userEntity = new UserEntity();
        when(userService.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // Act
        ResponseEntity<?> responseEntity = subscriptionController.changeSubscription(username, newPlanName);

        // Assert
        verify(subscriptionService).changeSubscription(any(UserEntity.class), eq(newPlanName));
        assert responseEntity.getStatusCode() == HttpStatus.OK;
    }

}
