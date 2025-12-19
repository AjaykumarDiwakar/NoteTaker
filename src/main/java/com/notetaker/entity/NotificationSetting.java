package com.notetaker.entity;

import java.time.LocalDateTime;

import com.notetaker.constant.NotificationCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tab_noti_setting_det")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "setting_id")
	private int id;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "setting_key", nullable = false)
	private String settingKey;

	@Column(name = "setting_value", nullable = false)
	private boolean settingValue;

	@Column(name = "category", nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationCategory category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
