package com.notetaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notetaker.entity.NotificationSetting;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer> {
	@Query("FROM NotificationSetting n WHERE n.category= :category AND n.userId= :userId")
	public List<NotificationSetting> getNotificationSettingsByCategoryForUser(@Param("category") String category,
			@Param("userId") String userId);

	@Query("FROM NotificationSetting n WHERE n.category= :category AND n.userId= :userId AND n.settingKey IN :keys")
	public List<NotificationSetting> getSettingByKeyAndCategoryForUser(@Param("category") String category,
			@Param("userId") String userId, @Param("keys") List<String> keys);
}
