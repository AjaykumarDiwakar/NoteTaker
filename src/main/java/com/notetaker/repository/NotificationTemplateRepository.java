package com.notetaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notetaker.constant.NotificationCategory;
import com.notetaker.entity.NotificationTemplates;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplates, Integer> {

	@Query("FROM NotificationTemplates n WHERE n.category= :category AND n.eventName= :eventName AND status=1")
	public List<NotificationTemplates> geNotificationTemplatesByCategoryAndEventName(@Param("category") NotificationCategory category,
			@Param("eventName") String eventName);
}
