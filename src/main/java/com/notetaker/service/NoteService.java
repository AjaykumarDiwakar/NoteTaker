package com.notetaker.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

public interface NoteService {

	public Object addNote(List<MultipartFile> files, String note, HttpServletRequest request);

	public Object getAllNotesOfUser(HttpServletRequest request);

	public Object updateNotes(List<MultipartFile> files, String note, Long noteId, HttpServletRequest request);

	public Object deleteNote(Long noteId, HttpServletRequest request);
}
