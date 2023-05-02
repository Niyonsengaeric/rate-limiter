package com.example.ratelimiter.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ratelimiter.Model.User;

public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByUserNameOrEmail(String userName, String email);

    User findByUserNameOrEmail(String username, String username1);

    User findByUserName(String user);
}
