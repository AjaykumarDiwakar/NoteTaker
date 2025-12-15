package com.notetaker.entity;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer movieId;

	@Column(nullable = false, length = 200)
	@NotBlank(message = "Please provide movie's title")
	private String title;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's director")
	private String director;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's studio")
	private String studio;

	@ElementCollection
	@CollectionTable(name = "movie_cast")
	private Set<String> movieCast;
	
	@NotNull(message = "Please provide movie's release year")
	private Integer releaseYear;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's poster")
	private String poster;

}
