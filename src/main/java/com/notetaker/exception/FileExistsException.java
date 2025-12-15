package com.notetaker.exception;

public class FileExistsException extends RuntimeException {

	public FileExistsException(String message) {
		super(message);
	}
}
