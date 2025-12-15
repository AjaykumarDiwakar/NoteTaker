package com.notetaker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.notetaker.auth.service.JwtService;
import com.notetaker.service.NoteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/note")
public class NoteController {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private NoteService noteService;

	@PostMapping("/add-notes")
	public ResponseEntity<?> addNotesHandler(@RequestPart List<MultipartFile> files, @RequestPart String note,
			HttpServletRequest request) {
		log.info("inside add note for user: " + jwtService
				.extractUsername(jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"))));
//		if (files != null || !files.isEmpty()) {
//			// save file related info --- means we are going to create Entity object for all
//			// the files and store the file in folder
//		}
//		//The above method service will return a list of ImageEntity and then we will create a object of Note Entity and save it

		return ResponseEntity.ok(noteService.addNote(files, note, request));
	}

}
