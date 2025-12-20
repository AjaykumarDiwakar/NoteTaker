package com.notetaker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notetaker.entity.NotificationHistoryDetail;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistoryDetail, Integer>{

}
