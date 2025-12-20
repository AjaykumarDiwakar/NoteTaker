package com.notetaker.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.notetaker.service.NotificationSettingService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notification")
public class NotificationSettingController {

	@Autowired
	private NotificationSettingService notificationSettingService;

	@GetMapping("/get-setting")
	public ResponseEntity<?> getAllNotificationSettings(@RequestParam("category") String category,
			HttpServletRequest request) {
		return ResponseEntity.ok(notificationSettingService.getSettingByCategory(category, request));
	}

	@PutMapping("/update-setting")
	public ResponseEntity<?> updateNotificationSetting(@RequestParam("category") String category,
			@RequestBody Map<String, Boolean> settings, HttpServletRequest request) {
		return ResponseEntity.ok(notificationSettingService.updateSettingByCategory(category, settings, request));
	}

}
