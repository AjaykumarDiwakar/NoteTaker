package com.notetaker.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.notetaker.auth.entity.UserRole;
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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tab_notification_template_det")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTemplates {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "template_id", nullable = false)
	@JsonProperty(value = "message_id")
	private int templateId;

//	@Column(name = "user_id", nullable = false)
//	@JsonProperty(value = "user_id")
//	private String userId;

	@Column(name = "event_name")
	@JsonProperty(value = "event_name")
	private String eventName;

	@Column(name = "subject")
	@JsonProperty(value = "subject")
	private String subject;

	@Column(name = "message_text")
	@JsonProperty(value = "message_text")
	private String messageText;

	@Column(name = "status")
	@JsonProperty(value = "status")
	private int status;

	@Enumerated(EnumType.STRING)
	@Column(name = "category")
	@JsonProperty(value = "category")
	private NotificationCategory category;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role")
	@JsonProperty(value = "user_role")
	private UserRole user_role;

	@Column(name = "created_at")
	@JsonProperty(value = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@JsonProperty(value = "updated_at")
	private LocalDateTime updatedAt;
}
