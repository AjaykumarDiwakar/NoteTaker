package com.notetaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notetaker.entity.NotesDetail;

public interface NotesDetailRepository extends JpaRepository<NotesDetail, Long>{

}
