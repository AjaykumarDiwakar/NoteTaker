package com.notetaker.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.notetaker.dto.MovieDto;
import com.notetaker.entity.Movie;
import com.notetaker.exception.FileExistsException;
import com.notetaker.exception.MovieNotFoundException;
import com.notetaker.repository.MovieRepository;


@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private FileService fileService;

	@Value("${project.poster}")
	private String path;

	@Value("${base.url}")
	private String baseUrl;

	@Override
	public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
		// 1.upload the file
		if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
			throw new FileExistsException("File already exists! Please enter another file name");
		}
		String uploadedFileName = fileService.uploadFile(path, file);

		// 2.set the value of field 'Poster' as filename
		movieDto.setPoster(uploadedFileName);

		// 3.map dto to movie Object
		Movie movie = Movie.builder().movieId(movieDto.getMovieId()).title(movieDto.getTitle())
				.director(movieDto.getDirector()).studio(movieDto.getStudio()).movieCast(movieDto.getMovieCast())
				.releaseYear(movieDto.getReleaseYear()).poster(movieDto.getPoster()).build();

		// 4. save the movie object-->Saved Movie Object
		Movie savedMovie = movieRepository.save(movie);

		// 5. generate the posterUrl:The Url by hitting which we will get the file
		String posterUrl = baseUrl + "/file/" + uploadedFileName;

		// 6. map Movie object to DTO object and return it
		MovieDto response = MovieDto.builder().movieId(savedMovie.getMovieId()).title(savedMovie.getTitle())
				.director(savedMovie.getDirector()).studio(savedMovie.getStudio()).movieCast(savedMovie.getMovieCast())
				.releaseYear(savedMovie.getReleaseYear()).poster(savedMovie.getPoster()).posterUrl(posterUrl).build();
		return response;
	}

	@Override
	public MovieDto getMovie(Integer movieId) {
		// 1.check the data in db if exists then fetch the data of given id
		Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not Found!"));

		// 2.generate posterurl
		String posterUrl = baseUrl + "/file/" + movie.getPoster();

		// 3 map to MovieDto object and returns it
		return MovieDto.builder().movieId(movie.getMovieId()).title(movie.getTitle()).director(movie.getDirector())
				.studio(movie.getStudio()).movieCast(movie.getMovieCast()).releaseYear(movie.getReleaseYear())
				.poster(movie.getPoster()).posterUrl(posterUrl).build();

	}

	@Override
	public List<MovieDto> getAllMovies() {
		// 1.fetch all data from DB
		List<Movie> movies = movieRepository.findAll();

		// 2.Iterate through the list ,generate posterUrl for each movie obj,and map to
		// MovieDto Obj
		List<MovieDto> response = new ArrayList<MovieDto>();

		for (Movie movie : movies) {
			String posterUrl = baseUrl + "/file/" + movie.getPoster();
			response.add(MovieDto.builder().movieId(movie.getMovieId()).title(movie.getTitle())
					.director(movie.getDirector()).studio(movie.getStudio()).movieCast(movie.getMovieCast())
					.releaseYear(movie.getReleaseYear()).poster(movie.getPoster()).posterUrl(posterUrl).build());
		}
		return response;
	}

	@Override
	public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
		// 1.check if movie object exists with given movieId
		Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie Not Found!"));

		// 2. If file is null, do nothing
		// if file is not null,then delete existing file associated with the record
		String fileName = movie.getPoster();
		if (file != null) {
			Files.deleteIfExists(Paths.get(path + File.separator + fileName));
			fileName = fileService.uploadFile(path, file);
		}

		// 3. set movieDto's poster value,according to setp2
		movieDto.setPoster(fileName);

		// 4.map it to Movie Object
		Movie movieObj = Movie.builder().movieId(movie.getMovieId()).title(movieDto.getTitle())
				.director(movieDto.getDirector()).studio(movieDto.getStudio()).movieCast(movieDto.getMovieCast())
				.releaseYear(movieDto.getReleaseYear()).poster(movieDto.getPoster()).build();

		// 5.save the movie object ->return saved movie Object
		Movie updatedMovie = movieRepository.save(movieObj);
		// 6. generate posterUrl for it
		String posterUrl = baseUrl + "/file/" + fileName;
		// 7. map to MovieDto
		MovieDto dto = MovieDto.builder().movieId(updatedMovie.getMovieId()).title(updatedMovie.getTitle())
				.director(updatedMovie.getDirector()).studio(updatedMovie.getStudio())
				.movieCast(updatedMovie.getMovieCast()).releaseYear(updatedMovie.getReleaseYear())
				.poster(updatedMovie.getPoster()).posterUrl(posterUrl).build();
		return dto;
	}

	@Override
	public String deleteMovie(Integer id) throws IOException {

		// 1.check if the movie exists
		Movie movie = movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException("Movie Not Found!"));

		// 2 delete the file associated with this object
		Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));

		// 3. Delete the movie object
		movieRepository.delete(movie);

		return "Movie deleted with id = " + id;
	}

}
