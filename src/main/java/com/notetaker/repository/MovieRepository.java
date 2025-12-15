package com.notetaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notetaker.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

}
