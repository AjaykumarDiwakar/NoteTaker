package com.notetaker.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.notetaker.auth.service.JwtService;
import com.notetaker.service.FileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file/")
public class FileController {

	@Autowired
	private FileService fileService;

	@Value("${project.poster}")
	private String path;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException {
		return ResponseEntity.ok(fileService.uploadFile(path, file));
	}

	@GetMapping("/{fileName}")
	public void serveFileHandler(@PathVariable("fileName") String fileName, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		String token = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		String userName = jwtService.extractUsername(token);

		InputStream resourceFile = fileService.getResourceFile(path + File.separator + userName, fileName);
		response.setContentType(MediaType.IMAGE_PNG_VALUE);
		StreamUtils.copy(resourceFile, response.getOutputStream());
	}

}
