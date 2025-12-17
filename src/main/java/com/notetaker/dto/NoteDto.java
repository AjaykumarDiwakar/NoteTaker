package com.notetaker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDto {

	private Long noteId;
	private String userId;
	private String title;
	private String body;

	// One note → multiple images
	private List<ImageDto> images;
}
