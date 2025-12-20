package com.notetaker.service;

import com.notetaker.dto.MailBody;
import com.notetaker.entity.NotificationTemplates;

public interface NotificationService {

	public NotificationTemplates getNotificationTemplateByEventName(String eventName, String category);

	public void addNotificationHistoryInDbForEmail(MailBody body, String userId, String eventName, String userRole);
}
