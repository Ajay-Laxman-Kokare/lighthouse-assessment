package com.aadharvalidation.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aadharvalidation.model.User;
import com.aadharvalidation.repository.UserRepository;
import com.aadharvalidation.util.JwtUtil;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
	}
	
	public String registerUser(String username, String Password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(Password);
		userRepository.save(user);
		return "User "+ user.getUsername()+" Registered Successfully";
	}
	
	public String authenticate(String username, String password) {
		Optional<User> user = userRepository.findByUsername(username);
		if(user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
			return jwtUtil.generateToken(username);
		}
		return "invalid";
	}
}
