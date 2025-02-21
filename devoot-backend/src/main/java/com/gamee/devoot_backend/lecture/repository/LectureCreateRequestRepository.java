package com.gamee.devoot_backend.lecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecture.entity.LectureCreateRequest;

public interface LectureCreateRequestRepository extends JpaRepository<LectureCreateRequest, Long> {
	void deleteBySourceUrl(String sourceUrl);
}
