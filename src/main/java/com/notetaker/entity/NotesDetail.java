package com.notetaker.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tab_notes_det")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotesDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "note_id")
	private Long noteId;

	@Column(name = "user_id", nullable = false)
	private String userId;

	private String title;

	@Column(columnDefinition = "TEXT")
	private String body;

	// One Note -> Multiple Images
	@OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ImageDetail> images = new ArrayList<>();

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "NotesDetail [noteId=" + noteId + ", userId=" + userId + ", title=" + title + ", body=" + body
				+ ", images=" + images + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
