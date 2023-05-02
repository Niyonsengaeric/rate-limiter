package com.example.ratelimiter.Repo;


import com.example.ratelimiter.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    boolean existsByUserNameOrEmail(String userName, String email);
}
