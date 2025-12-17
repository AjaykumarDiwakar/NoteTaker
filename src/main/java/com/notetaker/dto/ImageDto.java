package com.notetaker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDto {
	private Long imageId;
	private String imageName;
	private String imagePath;
	private String imageUrl;
}
