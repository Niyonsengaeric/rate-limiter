package com.example.ratelimiter.Service;

import org.springframework.stereotype.Service;

import com.example.ratelimiter.Model.User;

@Service
public interface UserService {
	User addUser(User user);

	boolean existsByUserNameOrEmail(String userName, String email);

	User findUserName(String user);
}
