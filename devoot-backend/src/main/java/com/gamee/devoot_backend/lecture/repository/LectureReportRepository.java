package com.gamee.devoot_backend.lecture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecture.entity.LectureReport;

public interface LectureReportRepository extends JpaRepository<LectureReport, Long> {
	Optional<LectureReport> findByLectureIdAndUserId(Long lectureId, Long userId);
}
