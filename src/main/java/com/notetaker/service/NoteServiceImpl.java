package com.notetaker.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.notetaker.auth.entity.UserRole;
import com.notetaker.auth.service.JwtService;
import com.notetaker.constant.NoteTakerConstants;
import com.notetaker.constant.NotificationCategory;
import com.notetaker.constant.NotificationSettingKeys;
import com.notetaker.dto.ImageDto;
import com.notetaker.dto.MailBody;
import com.notetaker.dto.NoteDto;
import com.notetaker.entity.ImageDetail;
import com.notetaker.entity.NotesDetail;
import com.notetaker.entity.NotificationTemplates;
import com.notetaker.repository.ImageDetailRepository;
import com.notetaker.repository.NotesDetailRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
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

	@Autowired
	private ImageDetailRepository imageDetailRepository;

	@Autowired
	private NotificationSettingService notificationSettingService;

	@Autowired
	private EmailService emailService;

	@Transactional
	@Override
	public Object addNote(List<MultipartFile> files, String note, HttpServletRequest request) {
		List<ImageDetail> images = new ArrayList<ImageDetail>();
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userName = jwtService.extractUsername(token);
		String userId = jwtService.extractUserId(token);
		ObjectMapper objectMapper = new ObjectMapper();
		NoteDto dto = objectMapper.readValue(note, NoteDto.class);
		if (notesDetailRepository.isNoteWithSameTitleAvailable(userId, dto.getTitle())) {
			throw new RuntimeException("Duplicate title is not allowed");
		}
		String email = jwtService.extractEmail(token);
		String name = jwtService.extractName(token);
		String role = UserRole.USER.name();
		if (files != null && !files.isEmpty()) {
			images = uploadFiles(files, userName, userId);
		}
		NotesDetail notesDetail = NotesDetail.builder().title(dto.getTitle()).body(dto.getBody()).userId(userId)
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).images(images).build();
		images.forEach(x -> x.setNote(notesDetail));
		NotesDetail savedNote = notesDetailRepository.save(notesDetail);
		sendAddNoteNotificationOnEmail(userId, List.of(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS,
				NotificationSettingKeys.EMAIL_ON_ADD_NOTE_KEY), email, notesDetail, name, role);
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
			String originalName = file.getOriginalFilename();
			String extension = originalName.substring(originalName.lastIndexOf("."));
			String fileName = UUID.randomUUID().toString();
			String filepath = rootPath + File.separator + fileName + extension;
			try {
				Files.copy(file.getInputStream(), Paths.get(filepath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String imageUrl = baseUrl + "/file/" + fileName + extension;
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

	@Override
	public Object getAllNotesOfUser(HttpServletRequest request) {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userId = jwtService.extractUserId(token);
		log.info("inside getting notes for the userid: " + userId);
		List<NotesDetail> notes = notesDetailRepository.getAllNotesByUserId(userId);
		List<NoteDto> notesDto = new ArrayList<>();
		for (NotesDetail note : notes) {
			notesDto.add(convertNoteEntityToDto(note));
		}
		return notesDto;
	}

	@Override
	public Object updateNotes(List<MultipartFile> files, String noteStr, Long noteId, HttpServletRequest request) {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userName = jwtService.extractUsername(token);
		String userId = jwtService.extractUserId(token);
		String email = jwtService.extractEmail(token);
		String name = jwtService.extractName(token);
		NotesDetail note = notesDetailRepository.findByIdAndUserId(noteId,userId)
				.orElseThrow(() -> new RuntimeException("Invalid note id"));
		List<ImageDetail> images = note.getImages();
		deleteImg(images);
		imageDetailRepository.deleteAll(images);
		List<ImageDetail> newImages = new ArrayList<ImageDetail>();
		if (files != null && !files.isEmpty()) {
			newImages = uploadFiles(files, userName, userId);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		NoteDto dto = objectMapper.readValue(noteStr, NoteDto.class);

		NotesDetail notesDetail = NotesDetail.builder().noteId(noteId).title(dto.getTitle()).body(dto.getBody())
				.userId(userId).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).images(newImages).build();
		newImages.forEach(x -> x.setNote(notesDetail));
		NotesDetail savedNote = notesDetailRepository.save(notesDetail);
		sendUpdateNoteNotificationOnEmail(userId,
				List.of(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS,
						NotificationSettingKeys.EMAIL_ON_UPDATE_NOTE_KEY),
				email, notesDetail, name, UserRole.USER.name());
		return convertNoteEntityToDto(savedNote);
	}

	@Override
	public Object deleteNote(Long noteId, HttpServletRequest request) {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userId = jwtService.extractUserId(token);
		String email = jwtService.extractEmail(token);
		String name = jwtService.extractName(token);
		NotesDetail note = notesDetailRepository.findByIdAndUserId(noteId,userId)
				.orElseThrow(() -> new RuntimeException("Invalid note id"));
		List<ImageDetail> images = note.getImages();
		deleteImg(images);
		notesDetailRepository.delete(note);
		sendDeleteNoteNotificationOnEmail(userId, List.of(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS,
				NotificationSettingKeys.EMAIL_ON_DELETE_NOTE_KEY), email, note, name, UserRole.USER.name());
		return "Success";
	}

	private void deleteImg(List<ImageDetail> images) {
		for (ImageDetail img : images) {
			try {
				Files.deleteIfExists(Paths.get(img.getImagePath()));
			} catch (IOException e) {
				log.info("getting error while deleting images from the folder");
				e.printStackTrace();
			}
		}
	}

	private void sendAddNoteNotificationOnEmail(String userId, List<String> keys, String email, NotesDetail notesDetail,
			String name, String role) {
		Map<String, Boolean> settingMap = notificationSettingService
				.getSettingByeKeyAndCategoryForUser(NotificationCategory.EMAIL.name(), userId, keys);
		if (settingMap.getOrDefault(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS, false)
				&& settingMap.getOrDefault(NotificationSettingKeys.EMAIL_ON_ADD_NOTE_KEY, false)) {
			Object[] args = { notesDetail.getTitle(), name, notesDetail.getCreatedAt() };
			emailService.sendEventBasedEmailNotification(userId, email, name, role, args,
					NoteTakerConstants.NOTIFICATION_ON_ADD_NOTE_EVENT);
		}
	}

	private void sendUpdateNoteNotificationOnEmail(String userId, List<String> keys, String email,
			NotesDetail notesDetail, String name, String role) {
		Map<String, Boolean> settingMap = notificationSettingService
				.getSettingByeKeyAndCategoryForUser(NotificationCategory.EMAIL.name(), userId, keys);
		if (settingMap.getOrDefault(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS, false)
				&& settingMap.getOrDefault(NotificationSettingKeys.EMAIL_ON_UPDATE_NOTE_KEY, false)) {
			Object[] args = { notesDetail.getTitle(), name, notesDetail.getCreatedAt() };
			emailService.sendEventBasedEmailNotification(userId, email, name, role, args,
					NoteTakerConstants.NOTIFICATION_ON_UPDATE_NOTE_EVENT);
		}
	}

	private void sendDeleteNoteNotificationOnEmail(String userId, List<String> keys, String email,
			NotesDetail notesDetail, String name, String role) {
		Map<String, Boolean> settingMap = notificationSettingService
				.getSettingByeKeyAndCategoryForUser(NotificationCategory.EMAIL.name(), userId, keys);
		if (settingMap.getOrDefault(NotificationSettingKeys.ALLOW_EMAIL_NOTIFICATIONS, false)
				&& settingMap.getOrDefault(NotificationSettingKeys.EMAIL_ON_DELETE_NOTE_KEY, false)) {
			Object[] args = { notesDetail.getTitle(), name, notesDetail.getCreatedAt() };
			emailService.sendEventBasedEmailNotification(userId, email, name, role, args,
					NoteTakerConstants.NOTIFICATION_ON_DELETE_NOTE_EVENT);
		}
	}

}
