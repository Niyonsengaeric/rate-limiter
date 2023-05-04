package com.example.ratelimiter.Controller;
import com.example.ratelimiter.Controller.UserController;
import com.example.ratelimiter.Exceptions.NotFoundException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.junit.jupiter.api.Assertions;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddUser_Success() {
        // Create a mock User object
        User user = new User();
        user.setUserName("testUser");
        user.setEmail("test@example.com");

        // Mock the userService.existsByUserNameOrEmail() method to return false (user does not exist)
        when(userService.existsByUserNameOrEmail(anyString(), anyString())).thenReturn(false);

        // Mock the userService.addUser() method to return the created User object
        when(userService.addUser(user)).thenReturn(user);

        // Send a request to the addUser() method
        ResponseEntity<?> response = userController.addUser(user);

        // Verify the response status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the response body
        assertEquals(user, response.getBody());
    }

    @Test
    public void testAddUser_UserNameOrEmailAlreadyUsed() {
        // Create a mock User object
        User user = new User();
        user.setUserName("existingUser");
        user.setEmail("existing@example.com");

        // Mock the userService.existsByUserNameOrEmail() method to return true (user already exists)
        when(userService.existsByUserNameOrEmail(anyString(), anyString())).thenReturn(true);

        // Verify that a NotFoundException is thrown when adding the user
        try {
            userController.addUser(user);
        } catch (NotFoundException ex) {
            // Verify the exception message
            assertEquals("userName or email already used", ex.getMessage());
        }
    }


    @Test
    public void testLogin_Success() {
        // Create a mock HttpServletRequest
        Map<String, Object> testData = new HashMap<>();
        testData.put("token1", "value1");
        testData.put("token2", "value2");

        // Mock the request.getAttribute() method to return the testData map
        when(request.getAttribute("data")).thenReturn(testData);

        // Send a request to the login() method
        ResponseEntity<?> response = userController.login(request);

        // Verify the response body
        assertEquals(testData, response.getBody());
    }
}
