package com.notetaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notetaker.entity.ImageDetail;

public interface ImageDetailRepository extends JpaRepository<ImageDetail, Long> {

}
