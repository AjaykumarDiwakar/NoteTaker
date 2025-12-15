package com.notetaker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tab_image_det")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ImageDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long imageId;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "image_path")
	private String imagePath; // file system path

	@Column(name = "image_url")
	private String imageUrl; // API access URL

	// Many Images -> One Note
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "note_id", nullable = false)
	private NotesDetail note;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Override
	public String toString() {
		return "ImageDetail [imageId=" + imageId + ", userId=" + userId + ", imageName=" + imageName + ", imagePath="
				+ imagePath + ", imageUrl=" + imageUrl + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
