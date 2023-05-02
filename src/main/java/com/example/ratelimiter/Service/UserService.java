package com.example.ratelimiter.Service;

import com.example.ratelimiter.Model.User;
import org.springframework.stereotype.Service;


@Service
public interface UserService {
	User addUser(User user);
	boolean existsByUserNameOrEmail( String userName, String email);
}
