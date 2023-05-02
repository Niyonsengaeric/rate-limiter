package com.example.ratelimiter.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Model.UserSubscription;

public interface UserSubscriptionRepo extends JpaRepository<UserSubscription, Integer> {
    UserSubscription findByUserAndStatusTrue(User user);

    boolean existsByUserAndStatusTrue(User user);

}
