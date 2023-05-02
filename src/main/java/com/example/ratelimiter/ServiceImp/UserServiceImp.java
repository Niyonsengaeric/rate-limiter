package com.example.ratelimiter.ServiceImp;

import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Repo.UserRepo;
import com.example.ratelimiter.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImp implements UserService {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	@Override
	public User addUser(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		return userRepo.save(user);
	}

	@Override
	public boolean existsByUserNameOrEmail(String email, String userName) {
		return userRepo.existsByUserNameOrEmail(userName,email);
	}
}
