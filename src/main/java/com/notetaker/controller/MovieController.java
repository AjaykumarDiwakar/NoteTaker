package com.notetaker.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.notetaker.dto.MovieDto;
import com.notetaker.service.MovieService;

import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

	@Autowired
	private MovieService movieService;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/add-movie")
	public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDto)
			throws IOException {
		return ResponseEntity.status(HttpStatus.CREATED).body(movieService.addMovie(convertToMovieDto(movieDto), file));
	}

	private MovieDto convertToMovieDto(String movieDto) {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(movieDto, MovieDto.class);
	}

	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId) {
		return ResponseEntity.ok(movieService.getMovie(movieId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<MovieDto>> getAllMoviesHandler() {
		return ResponseEntity.ok(movieService.getAllMovies());
	}

	@PutMapping("/update/{movieId}")
	public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId, @RequestPart MultipartFile file,
			@RequestPart String movieDtoObj) throws IOException {
		if (file.isEmpty())
			file = null;
		MovieDto movieDto = convertToMovieDto(movieDtoObj);
		return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));
	}

	@DeleteMapping("/delete/{movieId}")
	public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId, HttpServletRequest request)
			throws IOException {
		String authHeader = jwtService.extractActualTokenFromBearerAuth(request.getHeader("Authorization"));
		System.out.println(authHeader);
		System.out.println(jwtService.extractUsername(authHeader));
		return ResponseEntity.ok(movieService.deleteMovie(movieId));
	}
}
