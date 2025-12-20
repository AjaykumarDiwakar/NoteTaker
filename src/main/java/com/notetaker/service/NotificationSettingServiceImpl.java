package com.notetaker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.notetaker.auth.service.JwtService;
import com.notetaker.constant.NotificationCategory;
import com.notetaker.constant.NotificationSettingKeys;
import com.notetaker.entity.NotificationSetting;
import com.notetaker.repository.NotificationSettingRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class NotificationSettingServiceImpl implements NotificationSettingService {

	@Autowired
	private NotificationSettingRepository notificationSettingRepository;

	@Autowired
	private JwtService jwtService;

	@Override
	public Object getSettingByCategory(String category, HttpServletRequest request) {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userId = jwtService.extractUserId(token);
		log.info("fetching setting of category : " + category + " for userId : " + userId);
		List<NotificationSetting> settings = notificationSettingRepository
				.getNotificationSettingsByCategoryForUser(NotificationCategory.valueOf(category), userId);
		Map<String, Boolean> defaultSetting = NotificationSettingKeys.SETTING_MAP
				.get(NotificationCategory.valueOf(category));
		Map<String, Boolean> settingMap = settings.stream().collect(
				Collectors.toMap(NotificationSetting::getSettingKey, NotificationSetting::isSettingValue, (x, y) -> x));
		// add default setting if missing in the db
		for (Map.Entry<String, Boolean> entry : defaultSetting.entrySet()) {
			String key = entry.getKey();
			if (!settingMap.containsKey(key)) {
				settingMap.put(key, false);
			}
		}
		return settingMap;
	}

	@Override
	public Object updateSettingByCategory(String category, Map<String, Boolean> settings, HttpServletRequest request) {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userId = jwtService.extractUserId(token);
		log.info("updating setting of category : " + category + " for userId : " + userId);
		Map<String, Boolean> defaultSetting = NotificationSettingKeys.SETTING_MAP
				.get(NotificationCategory.valueOf(category));

		for (Map.Entry<String, Boolean> entry : settings.entrySet()) {
			defaultSetting.put(entry.getKey(), entry.getValue());
		}

		List<NotificationSetting> dbSettings = notificationSettingRepository
				.getNotificationSettingsByCategoryForUser(NotificationCategory.valueOf(category), userId);
		Map<String, NotificationSetting> dbSettingMap = dbSettings.stream()
				.collect(Collectors.toMap(NotificationSetting::getSettingKey, Function.identity(), (x, y) -> x));
		for (Map.Entry<String, Boolean> entry : defaultSetting.entrySet()) {
			String key = entry.getKey();
			Boolean value = entry.getValue();
			if (dbSettingMap.containsKey(key)) {
				NotificationSetting entity = dbSettingMap.get(key);
				entity.setSettingValue(value);
				entity.setUpdatedAt(LocalDateTime.now());
				dbSettingMap.put(key, entity);
			} else {
				NotificationSetting entity = createNotificationSettingEntity(key, value, userId, category);
				dbSettingMap.put(key, entity);
			}
		}
		notificationSettingRepository.saveAll(dbSettingMap.entrySet().stream().map(x -> x.getValue()).toList());

		return "Success";
	}

	private NotificationSetting createNotificationSettingEntity(String key, Boolean value, String userId,
			String category) {
		return NotificationSetting.builder().settingKey(key).settingValue(value).userId(userId)
				.category(NotificationCategory.valueOf(category)).createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now()).build();
	}

	@Override
	public Map<String, Boolean> getSettingByeKeyAndCategoryForUser(String category, String userId, List<String> keys) {

		return notificationSettingRepository
				.getSettingByKeyAndCategoryForUser(NotificationCategory.valueOf(category), userId, keys).stream()
				.collect(Collectors.toMap(x -> x.getSettingKey(), x -> x.isSettingValue(), (a, b) -> a));
	}

}
