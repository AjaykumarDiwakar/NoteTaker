package com.notetaker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notetaker.entity.NotesDetail;

public interface NotesDetailRepository extends JpaRepository<NotesDetail, Long> {

	@Query("FROM NotesDetail n WHERE n.userId= :userId")
	public List<NotesDetail> getAllNotesByUserId(@Param("userId") String userId);

	@Query("SELECT (COUNT(n)>0) FROM NotesDetail n WHERE n.userId= :userId AND n.title= :title")
	public Boolean isNoteWithSameTitleAvailable(@Param("userId") String userId, @Param("title") String title);

	@Query("From NotesDetail n WHERE n.noteId= :id AND n.userId= :userId")
	public Optional<NotesDetail> findByIdAndUserId(@Param("id") Long id, @Param("userId") String userId);
}
