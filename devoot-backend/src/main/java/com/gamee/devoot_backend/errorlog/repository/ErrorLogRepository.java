package com.gamee.devoot_backend.errorlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.errorlog.entity.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Integer> {
}
