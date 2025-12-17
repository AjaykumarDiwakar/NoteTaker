package com.notetaker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notetaker.entity.NotesDetail;

public interface NotesDetailRepository extends JpaRepository<NotesDetail, Long> {

	@Query("FROM NotesDetail n WHERE n.userId= :userId")
	public List<NotesDetail> getAllNotesByUserId(@Param("userId") String userId);
}
