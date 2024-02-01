package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
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


    @Test
    public void testChangeSubscription_InternalServerError() {
        // Arrange
        String username = "testUser";
        String newPlanName = "newPlan";

        when(userService.findByUsername(username)).thenReturn(Optional.of(new UserEntity()));
        doThrow(new RuntimeException("Simulated error")).when(subscriptionService).changeSubscription(any(), any());

        // Act
        ResponseEntity<?> responseEntity = subscriptionController.changeSubscription(username, newPlanName);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert responseEntity.getBody().equals("Error changing subscription: Simulated error");
    }

    @Test
    public void testGetSubscriptionDetails_Success() {
        // Arrange
        String username = "testUser";
        UserEntity userEntity = new UserEntity();
        Map<String, Object> subscriptionDetails = new HashMap<>();
        when(userService.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(subscriptionService.getCurrentSubscriptionDetails(userEntity)).thenReturn(subscriptionDetails);

        // Act
        ResponseEntity<Object> responseEntity = subscriptionController.getSubscriptionDetails(username);

        // Assert
        verify(subscriptionService).getCurrentSubscriptionDetails(any(UserEntity.class));
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody() == subscriptionDetails;
    }



    @Test
    public void testGetSubscriptionDetails_InternalServerError() {
        // Arrange
        String username = "testUser";
        when(userService.findByUsername(username)).thenReturn(Optional.of(new UserEntity()));
        doThrow(new RuntimeException("Simulated error")).when(subscriptionService).getCurrentSubscriptionDetails(any());

        // Act
        ResponseEntity<Object> responseEntity = subscriptionController.getSubscriptionDetails(username);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert responseEntity.getBody().equals("Internal Server Error");
    }
}
