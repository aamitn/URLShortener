package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;


    @InjectMocks
    private UserController userController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetUserDetails_Success() {
        // Arrange
        String username = "testUser";

        // Mocking methods
        when(userService.getUserDetailsByUsername(username)).thenReturn(new UserDetailsDto());

        // Act
        ResponseEntity<UserDetailsDto> responseEntity = userController.getUserDetails(username);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody() != null;
    }


    @Test
    public void testUpdateUserProfile_Success() {
        // Arrange
        String username = "testUser";
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        // Mocking methods
        when(userService.updateUserDetailsByUsername(username, updateUserRequest)).thenReturn(new UserEntity());

        // Act
        ResponseEntity<String> responseEntity = userController.updateUserProfile(username, updateUserRequest);

        // Assert
        assert responseEntity.getStatusCode() == HttpStatus.OK;
        assert responseEntity.getBody().contains("User profile updated successfully");
    }

}
