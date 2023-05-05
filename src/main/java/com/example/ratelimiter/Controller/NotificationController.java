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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private static final String REDIS_KEY_PREFIX = "time-rate_limit:";
    private static final String REDIS_MONTHLY_KEY_PREFIX = "monthly_rate_limit:";


    @Value("${default.time.capacity}")
    private int defaultTimeCapacity;

    @Value("${default.monthly.capacity}")
    private int defaultMonthlyCapacity;

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
        String monthlyRedisKey = REDIS_MONTHLY_KEY_PREFIX + userName + ":" + YearMonth.now().toString();

        String timeCapacity = redisTemplate.opsForValue().get(redisKey);
        String monthlyTimeCapacity = redisTemplate.opsForValue().get(monthlyRedisKey);

        if (timeCapacity == null) {
            timeCapacity = getUserTimeCapacity(userName);
            redisTemplate.opsForValue().set(redisKey, timeCapacity, 1, TimeUnit.MINUTES);
        }

        if (monthlyTimeCapacity == null) {
            monthlyTimeCapacity = String.valueOf(defaultMonthlyCapacity);
            setMonthlyCapacity(monthlyRedisKey, monthlyTimeCapacity);
        }

        int maxCapacity = Integer.parseInt(timeCapacity);
        int monthlyMaxCapacity = Integer.parseInt(monthlyTimeCapacity);

        if (maxCapacity <= 0 || monthlyMaxCapacity <= 0) {
            String errorMessage = maxCapacity <= 0 ? "Too many requests for user " + userName + ". Please try again later."
                    : "Too many requests for user " + userName + " per month. Please try again later.";

            Map<String, String> response = new HashMap<>();
            response.put("message", errorMessage);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }

        // Process the notification ...

        updateCapacity(redisKey, maxCapacity);
        updateCapacity(monthlyRedisKey, monthlyMaxCapacity);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification sent successfully");
        return ResponseEntity.ok(response);
    }

    private String getUserTimeCapacity(String userName) {
        User user = userService.findUserName(userName);
        UserSubscription subscription = userSubscriptionService.findUserSubscription(user);
        return subscription != null ? String.valueOf(subscription.getCapacity()) : String.valueOf(defaultTimeCapacity);
    }


    private void setMonthlyCapacity(String monthlyRedisKey, String monthlyTimeCapacity) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();
        LocalDate currentDate = LocalDate.now();

        long remainingDays = ChronoUnit.DAYS.between(currentDate, lastDayOfMonth) + 1;
        redisTemplate.opsForValue().set(monthlyRedisKey, monthlyTimeCapacity, remainingDays, TimeUnit.DAYS);
    }

    private void updateCapacity(String redisKey, int maxCapacity) {
        if (redisTemplate.hasKey(redisKey) && redisTemplate.getExpire(redisKey) > 0) {
            int newCapacity = maxCapacity - 1;
            long expirationTime = redisTemplate.getExpire(redisKey);
            redisTemplate.opsForValue().set(redisKey, String.valueOf(newCapacity), expirationTime, TimeUnit.MINUTES);
        }
    }

}
