package com.gamee.devoot_backend.lecturereview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.lecturereview.entity.LectureReviewReport;

public interface LectureReviewReportRepository extends JpaRepository<LectureReviewReport, Long> {
	Optional<LectureReviewReport> findByLectureReviewIdAndUserId(Long lectureReviewId, Long userId);

	@Modifying
	@Transactional
	@Query(value = """
		DELETE r FROM lecturereviewreport r
		INNER JOIN lecturereview lr ON lr.id = r.lectureReviewId
		WHERE lr.userId = :userId
		""", nativeQuery = true)
	void deleteByUserId(Long userId);

	void deleteByLectureReviewId(Long lectureReviewId);
}
