package com.notetaker.auth.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.notetaker.auth.entity.User;
import com.notetaker.auth.entity.UserRole;
import com.notetaker.auth.repository.UserRepository;
import com.notetaker.auth.utils.AuthResponse;
import com.notetaker.auth.utils.LoginRequest;
import com.notetaker.auth.utils.RegisterRequest;
import com.notetaker.constant.NoteTakerConstants;
import com.notetaker.constant.NotificationCategory;
import com.notetaker.constant.NotificationSettingKeys;
import com.notetaker.service.EmailService;
import com.notetaker.service.NotificationSettingService;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final AuthenticationManager authenticationManager;
	private final NotificationSettingService notificationSettingService;
	private final EmailService emailService;

	public AuthResponse register(RegisterRequest registerRequest, HttpServletRequest request) {
		var user = User.builder().name(registerRequest.getName()).email(registerRequest.getEmail())
				.actualUsername(registerRequest.getUsername())
				.password(passwordEncoder.encode(registerRequest.getPassword())).userRole(UserRole.USER).build();

		User savedUser = userRepository.save(user);
		var accessToken = jwtService.generateToken(savedUser);
		var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());
		sendRegisterEmailNotification(savedUser, NotificationSettingKeys.EMAIL_ON_USER_REGISTRATION,
				request.getHeader("User-Agent"), NoteTakerConstants.NOTIFICATION_ON_USER_REGISTRATION_EVENT);
		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getRefreshToken())
				.name(savedUser.getName()).email(savedUser.getEmail()).build();
	}

	public AuthResponse login(LoginRequest loginRequest, HttpServletRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		var user = userRepository.findByEmail(loginRequest.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
		var accessToken = jwtService.generateToken(user);
		var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());
		sendLoginEmailNotification((User) user,
				List.of(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS, NotificationSettingKeys.EMAIL_ON_LOGIN_KEY),
				request.getHeader("User-Agent"), NoteTakerConstants.NOTIFICATION_ON_LOGIN_EVENT);
		return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken.getRefreshToken())
				.name(user.getName()).email(user.getEmail()).build();
	}

	private void sendLoginEmailNotification(User user, List<String> keys, String deviceDetail, String eventName) {
		Map<String, Boolean> settingMap = notificationSettingService
				.getSettingByeKeyAndCategoryForUser(NotificationCategory.EMAIL.name(), user.getId(), keys);
		if (settingMap.getOrDefault(keys.get(0), false) && settingMap.getOrDefault(keys.get(1), false)) {
			Object[] args = { user.getName(), LocalDateTime.now(), deviceDetail };
			emailService.sendEventBasedEmailNotification(user.getId(), user.getEmail(), user.getName(),
					UserRole.USER.name(), args, NoteTakerConstants.NOTIFICATION_ON_LOGIN_EVENT);
		}
	}

	private void sendRegisterEmailNotification(User user, String key, String deviceDetail, String eventName) {
		Map<String, Boolean> settingMap = notificationSettingService
				.getSettingByeKeyAndCategoryForUser(NotificationCategory.EMAIL.name(), UserRole.ADMIN.name(), List.of(key));
		if (settingMap.getOrDefault(key, false)) {
			Object[] args = { user.getName(),user.getActualUsername(),user.getEmail() ,LocalDateTime.now()};
			emailService.sendEventBasedEmailNotification(user.getId(), user.getEmail(), user.getName(),
					UserRole.USER.name(), args, NoteTakerConstants.NOTIFICATION_ON_USER_REGISTRATION_EVENT);
		}
	}
}