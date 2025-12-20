package com.notetaker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.notetaker.auth.entity.UserRole;
import com.notetaker.constant.NotificationCategory;
import com.notetaker.dto.MailBody;
import com.notetaker.entity.NotificationHistoryDetail;
import com.notetaker.entity.NotificationTemplates;
import com.notetaker.repository.NotificationHistoryRepository;
import com.notetaker.repository.NotificationTemplateRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationTemplateRepository notificationTemplateRepository;

	@Autowired
	private NotificationHistoryRepository notificationHistoryRepository;

	@Override
	public NotificationTemplates getNotificationTemplateByEventName(String eventName, String category) {
		log.info("inside fetching Notification template of category: " + category + " and event name: " + eventName);
		List<NotificationTemplates> temps = notificationTemplateRepository
				.geNotificationTemplatesByCategoryAndEventName(NotificationCategory.valueOf(category), eventName);
		if (temps.isEmpty()) {
			return null;
		}
		return temps.get(0);
	}

	@Override
	public void addNotificationHistoryInDbForEmail(MailBody body, String userId, String eventName, String userRole) {
		log.info("saving notification history for user: " + userId + " and the event name is : " + eventName);
		NotificationHistoryDetail historyDetail = NotificationHistoryDetail.builder()
				.category(NotificationCategory.EMAIL).createdAt(LocalDateTime.now()).eventName(eventName)
				.messageText(body.getText()).subject(body.getSubject()).user_role(UserRole.valueOf(userRole))
				.userId(userId).build();
		notificationHistoryRepository.save(historyDetail);
	}

}
