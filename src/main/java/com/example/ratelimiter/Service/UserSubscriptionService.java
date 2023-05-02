package com.example.ratelimiter.Service;

import org.springframework.stereotype.Service;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;

@Service
public interface UserSubscriptionService {
    Object addSubscription(UserSubscription user);
    UserSubscription findUserSubscription(User getUser);
    boolean isSubscribed(User getUser);
}
