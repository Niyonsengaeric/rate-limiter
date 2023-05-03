package com.example.ratelimiter.Controller;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;
import com.example.ratelimiter.Service.UserService;
import com.example.ratelimiter.Service.UserSubscriptionService;
import com.example.ratelimiter.filter.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private static final String REDIS_KEY_PREFIX = "rate_limit:";

    @Value("${default.time.capacity:5}")
    private int defaultTimeCapacity;

    @Autowired
    private UserSubscriptionService userSubscriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/notification")
    public ResponseEntity<Map<String, String>> sendNotification(HttpServletRequest request) {
        String userName = request.getAttribute(CustomAuthorizationFilter.userName).toString();
        String redisKey = REDIS_KEY_PREFIX + userName;

        String timeCapacity = redisTemplate.opsForValue().get(redisKey);
        if (timeCapacity == null) {
            User user = userService.findUserName(userName);
            UserSubscription subscription = userSubscriptionService.findUserSubscription(user);
            timeCapacity = subscription != null ? String.valueOf(subscription.getCapacity()) : String.valueOf(defaultTimeCapacity);
            redisTemplate.opsForValue().set(redisKey, timeCapacity, 1, TimeUnit.MINUTES);
        }

        int maxCapacity = Integer.parseInt(timeCapacity);
        if (maxCapacity <= 0) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Too many requests for user " + userName + ". Please try again later.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }

        // Process the notification ...

        if (redisTemplate.hasKey(redisKey) && redisTemplate.getExpire(redisKey) > 0) {
            int newCapacity = maxCapacity - 1;
            long expirationTime = redisTemplate.getExpire(redisKey);
            redisTemplate.opsForValue().set(redisKey, String.valueOf(newCapacity), expirationTime, TimeUnit.SECONDS);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification sent successfully");
        return ResponseEntity.ok(response);
    }
}
