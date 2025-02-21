package com.gamee.devoot_backend.lecture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamee.devoot_backend.lecture.entity.LectureUpdateRequest;

public interface LectureUpdateRequestRepository extends JpaRepository<LectureUpdateRequest, Long> {
	Optional<LectureUpdateRequest> findByLectureId(Long lectureId);

	void deleteByLectureId(Long lectureId);
}
