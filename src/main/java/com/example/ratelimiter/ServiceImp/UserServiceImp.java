package com.example.ratelimiter.ServiceImp;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ratelimiter.Exceptions.AuthException;
import com.example.ratelimiter.Model.User;
import com.example.ratelimiter.Repo.UserRepo;
import com.example.ratelimiter.Service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImp implements UserService, UserDetailsService {
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
		return userRepo.existsByUserNameOrEmail(userName, email);
	}

	@Override
	public User findUserName(String user) {
		return userRepo.findByUserName(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepo.findByUserNameOrEmail(username, username);
		if (!user.isStatus())
			throw new AuthException("user can not logged into the system please contact system admin");
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
				authorities);
	}
}
