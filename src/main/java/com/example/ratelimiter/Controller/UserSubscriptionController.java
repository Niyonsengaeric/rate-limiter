package com.example.ratelimiter.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ratelimiter.Exceptions.ConflictException;
import com.example.ratelimiter.Exceptions.NotFoundException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;
import com.example.ratelimiter.Service.UserService;
import com.example.ratelimiter.Service.UserSubscriptionService;
import com.example.ratelimiter.filter.CustomAuthorizationFilter;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class UserSubscriptionController {

    @Autowired
    UserSubscriptionService userSubscriptionService;

    @Autowired
    UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody UserSubscription user, HttpServletRequest request) {
        String userName = request.getAttribute(CustomAuthorizationFilter.userName).toString();
        User getUser = userService.findUserName(userName);
        if (userSubscriptionService.isSubscribed(getUser))
            throw new ConflictException("you already have a subscription");
        if (getUser == null)
            throw new NotFoundException("user not found");
        user.setUser(getUser);
        user.setCapacity(user.getCapacity());
        String redisKey = "time-rate_limit:" + userName;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(user.getCapacity()), 1, TimeUnit.SECONDS);
        return new ResponseEntity<>(userSubscriptionService.addSubscription(user), HttpStatus.CREATED);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<?> getSubscription(HttpServletRequest request) {
        String userName = request.getAttribute(CustomAuthorizationFilter.userName).toString();
        User getUser = userService.findUserName(userName);
        if (getUser == null)
            throw new NotFoundException("user not found");
        UserSubscription subscription = userSubscriptionService.findUserSubscription(getUser);
        if (subscription == null)
            throw new NotFoundException("no subscription found");
        return new ResponseEntity<>(subscription, HttpStatus.OK);
    }

}
