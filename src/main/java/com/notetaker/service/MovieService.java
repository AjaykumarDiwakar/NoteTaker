package com.notetaker.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.notetaker.dto.MovieDto;

public interface MovieService {

	MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

	MovieDto getMovie(Integer movieId);

	List<MovieDto> getAllMovies();

	MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

	String deleteMovie(Integer id) throws IOException;
}
