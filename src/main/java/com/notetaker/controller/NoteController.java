package com.notetaker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
		return ResponseEntity.ok(noteService.addNote(files, note, request));
	}

	@GetMapping("/get-all-notes")
	public ResponseEntity<?> getAllNotesOfUser(HttpServletRequest request) {
		return ResponseEntity.ok(noteService.getAllNotesOfUser(request));
	}

	@PutMapping("/update-note/{note_id}")
	public ResponseEntity<?> updateNoteHandler(@PathVariable("note_id") Long noteId,
			@RequestPart List<MultipartFile> files, @RequestPart String note, HttpServletRequest request) {
		return ResponseEntity.ok(noteService.updateNotes(files, note, noteId, request));
	}

	@DeleteMapping("/delete-note/{note_id}")
	public ResponseEntity<?> deleteNoteHandler(@PathVariable("note_id") Long noteId, HttpServletRequest request) {
		return ResponseEntity.ok(noteService.deleteNote(noteId, request));
	}
}
