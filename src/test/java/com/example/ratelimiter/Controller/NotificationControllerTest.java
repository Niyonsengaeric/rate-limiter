package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;
import com.example.ratelimiter.Service.UserService;
import com.example.ratelimiter.Service.UserSubscriptionService;
import com.example.ratelimiter.filter.CustomAuthorizationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.redis.core.ValueOperations;



import javax.servlet.http.HttpServletRequest;
import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


public class NotificationControllerTest {

    @Mock
    private UserSubscriptionService userSubscriptionService;

    @Mock
    private UserService userService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }



    @Test
    public void testSendNotification_Success() {
        // Mock the HttpServletRequest
        String userName = "testUser";
        when(request.getAttribute(CustomAuthorizationFilter.userName)).thenReturn(userName);

        // Mock RedisTemplate opsForValue
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        // Mock Redis values
        String redisKey = "time-rate_limit:" + userName;
        String monthlyRedisKey = "monthly_rate_limit:" + userName + ":" + YearMonth.now().toString();
        when(valueOperationsMock.get(redisKey)).thenReturn("2");
        when(valueOperationsMock.get(monthlyRedisKey)).thenReturn("10");

        // Mock user subscription
        User user = new User();
        user.setUserName(userName);
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setCapacity(5);
        when(userService.findUserName(userName)).thenReturn(user);
        when(userSubscriptionService.findUserSubscription(user)).thenReturn(subscription);

        // Send a request to the sendNotification() method
        ResponseEntity<Map<String, String>> response = notificationController.sendNotification(request);

        // Verify the response status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Notification sent successfully", responseBody.get("message"));
    }

    @Test
    public void testSendNotification_TimeCapacityExceeded() {
        // Mock the HttpServletRequest
        String userName = "testUser";
        when(request.getAttribute(CustomAuthorizationFilter.userName)).thenReturn(userName);

        // Mock RedisTemplate opsForValue
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        // Mock Redis values
        String redisKey = "time-rate_limit:" + userName;
        String monthlyRedisKey = "monthly_rate_limit:" + userName + ":" + YearMonth.now().toString();
        when(valueOperationsMock.get(redisKey)).thenReturn("0");
        when(valueOperationsMock.get(monthlyRedisKey)).thenReturn("10");

        // Mock user subscription
        User user = new User();
        user.setUserName(userName);
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setCapacity(5);
        when(userService.findUserName(userName)).thenReturn(user);
        when(userSubscriptionService.findUserSubscription(user)).thenReturn(subscription);

        // Send a request to the sendNotification() method
        ResponseEntity<Map<String, String>> response = notificationController.sendNotification(request);

        // Verify the response status code
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());

        // Verify the response body
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Too many requests for user testUser. Please try again later.", responseBody.get("message"));
    }

    @Test
    public void testSendNotification_MonthlyCapacityExceeded() {
        // Mock the HttpServletRequest
        String userName = "testUser";
        when(request.getAttribute(CustomAuthorizationFilter.userName)).thenReturn(userName);

        // Mock RedisTemplate opsForValue
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);

        // Mock Redis values
        String redisKey = "time-rate_limit:" + userName;
        String monthlyRedisKey = "monthly_rate_limit:" + userName + ":" + YearMonth.now().toString();
        when(valueOperationsMock.get(redisKey)).thenReturn("10");
        when(valueOperationsMock.get(monthlyRedisKey)).thenReturn("0");

        // Mock user subscription
        User user = new User();
        user.setUserName(userName);
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setCapacity(20);
        when(userService.findUserName(userName)).thenReturn(user);
        when(userSubscriptionService.findUserSubscription(user)).thenReturn(subscription);

        // Send a request to the sendNotification() method
        ResponseEntity<Map<String, String>> response = notificationController.sendNotification(request);

        // Verify the response status code
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());

        // Verify the response body
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Too many requests for user testUser per month. Please try again later.", responseBody.get("message"));
    }


    @Test
    public void testSendNotification_SetMonthlyCapacity() {
        // Mock the HttpServletRequest
        String userName = "testUser";
        when(request.getAttribute(CustomAuthorizationFilter.userName)).thenReturn(userName);

        // Mock Redis values
        String redisKey = "time-rate_limit:" + userName;
        String monthlyRedisKey = "monthly_rate_limit:" + userName + ":" + YearMonth.now().toString();
        when(redisTemplate.opsForValue().get(redisKey)).thenReturn("10");
        when(redisTemplate.opsForValue().get(monthlyRedisKey)).thenReturn("0");

        // Mock user subscription
        User user = new User();
        user.setUserName(userName);
        UserSubscription subscription = new UserSubscription();
        subscription.setUser(user);
        subscription.setCapacity(20);
        when(userService.findUserName(userName)).thenReturn(user);
        when(userSubscriptionService.findUserSubscription(user)).thenReturn(subscription);

        // Send a request to the sendNotification() method
        ResponseEntity<Map<String, String>> response = notificationController.sendNotification(request);

        // Verify the response status code
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());

        // Verify the response body
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Too many requests for user testUser per month. Please try again later.", responseBody.get("message"));
    }

}