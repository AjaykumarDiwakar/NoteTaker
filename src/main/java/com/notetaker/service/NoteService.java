package com.notetaker.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

public interface NoteService {

	public Object addNote(List<MultipartFile> files, String note, HttpServletRequest request);
}
