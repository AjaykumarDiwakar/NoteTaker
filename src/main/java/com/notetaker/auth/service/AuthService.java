package com.notetaker.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.notetaker.auth.entity.User;
import com.notetaker.auth.entity.UserRole;
import com.notetaker.auth.repository.UserRepository;
import com.notetaker.auth.utils.AuthResponse;
import com.notetaker.auth.utils.LoginRequest;
import com.notetaker.auth.utils.RegisterRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationManager authenticationManager;

	public AuthResponse register(RegisterRequest registerRequest) {
		var user = User.builder().name(registerRequest.getName()).email(registerRequest.getEmail())
				.actualUsername(registerRequest.getUsername())
				.password(passwordEncoder.encode(registerRequest.getPassword())).userRole(UserRole.USER).build();

		User savedUser = userRepository.save(user);
		var accessToken = jwtService.generateToken(savedUser);
		var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getRefreshToken())
				.name(savedUser.getName()).email(savedUser.getEmail()).build();
	}

	public AuthResponse login(LoginRequest loginRequest) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		var user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
		var accessToken = jwtService.generateToken(user);
		var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getRefreshToken())
				.name(user.getName()).email(user.getEmail()).build();
	}
}