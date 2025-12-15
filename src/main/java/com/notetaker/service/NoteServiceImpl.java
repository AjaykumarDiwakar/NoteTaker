package com.notetaker.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.notetaker.auth.service.JwtService;
import com.notetaker.dto.ImageDto;
import com.notetaker.dto.NoteDto;
import com.notetaker.entity.ImageDetail;
import com.notetaker.entity.NotesDetail;
import com.notetaker.repository.NotesDetailRepository;

import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class NoteServiceImpl implements NoteService {

	@Value("${project.poster}")
	private String rootFolder;

	@Value("${base.url}")
	private String baseUrl;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private NotesDetailRepository notesDetailRepository;

	@Override
	public Object addNote(List<MultipartFile> files, String note, HttpServletRequest request) {
		List<ImageDetail> images = new ArrayList<ImageDetail>();
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userName = jwtService.extractUsername(token);
		String userId = jwtService.extractUserId(token);
		String email = jwtService.extractEmail(token);
		if (files != null && !files.isEmpty()) {
			images = uploadFiles(files, userName, userId);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		NoteDto dto = objectMapper.readValue(note, NoteDto.class);
		NotesDetail notesDetail = NotesDetail.builder().title(dto.getTitle()).body(dto.getBody()).userId(userId)
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).images(images).build();
		images.forEach(x -> x.setNote(notesDetail));
		NotesDetail savedNote = notesDetailRepository.save(notesDetail);
		return convertNoteEntityToDto(savedNote);
	}

	private List<ImageDetail> uploadFiles(List<MultipartFile> files, String userName, String userId) {
		List<ImageDetail> list = new ArrayList<ImageDetail>();
		String rootPath = rootFolder + File.separator + userName;
		File f = new File(rootPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		for (MultipartFile file : files) {
			String filepath = rootPath + File.separator + file.getOriginalFilename();
			try {
				Files.copy(file.getInputStream(), Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String imageUrl = baseUrl + "/file/" + file.getOriginalFilename();
			ImageDetail m = ImageDetail.builder().imageName(file.getOriginalFilename()).imagePath(filepath)
					.imageUrl(imageUrl).userId(userId).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
					.build();
			list.add(m);
		}
		return list;
	}

	private NoteDto convertNoteEntityToDto(NotesDetail details) {
		List<ImageDetail> images = details.getImages();
		List<ImageDto> imgDtos = new ArrayList<ImageDto>();
		if (images != null && !images.isEmpty()) {
			for (ImageDetail img : images) {
				imgDtos.add(ImageDto.builder().imageId(img.getImageId()).imageName(img.getImageName())
						.imagePath(img.getImagePath()).imageUrl(img.getImageUrl()).build());
			}
		}
		return NoteDto.builder().body(details.getBody()).noteId(details.getNoteId()).title(details.getTitle())
				.userId(details.getUserId()).images(imgDtos).build();
	}

}
