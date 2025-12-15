package com.notetaker.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	// upload the file

	String uploadFile(String path, MultipartFile file) throws IOException;

	InputStream getResourceFile(String path, String name) throws FileNotFoundException;
}
