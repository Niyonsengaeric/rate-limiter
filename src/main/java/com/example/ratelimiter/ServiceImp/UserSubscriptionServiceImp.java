package com.example.ratelimiter.ServiceImp;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;
import com.example.ratelimiter.Repo.UserSubscriptionRepo;
import com.example.ratelimiter.Service.UserSubscriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionServiceImp implements UserSubscriptionService {

    private final UserSubscriptionRepo subscriptionRepo;
    @Override
    public Object addSubscription(UserSubscription subscription) {
        return subscriptionRepo.save(subscription);
    }

    @Override
    public UserSubscription findUserSubscription(User getUser) {
        return subscriptionRepo.findByUserAndStatusTrue(getUser);
    }

    @Override
    public boolean isSubscribed(User getUser) {
        return subscriptionRepo.existsByUserAndStatusTrue(getUser);
    }
}
