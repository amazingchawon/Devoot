package com.gamee.devoot_backend.lecturereview.repository;

import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gamee.devoot_backend.lecturereview.entity.LectureReviewReport;

@DataJpaTest
public class LectureReviewReportRepositoryTest {
	@Autowired
	private LectureReviewReportRepository lectureReviewReportRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Test findByLectureReviewIdAndUserId() ")
	public void findByLectureReviewIdAndUserId() {
		// Given
		Long userId = 1L, lectureReviewId = 1L;
		lectureReviewReportRepository.save(
			LectureReviewReport.builder()
				.lectureReviewId(lectureReviewId)
				.userId(userId)
				.build()
		);

		// When
		Optional<LectureReviewReport> report = lectureReviewReportRepository.findByLectureReviewIdAndUserId(lectureReviewId, userId);

		// Then
		assert (report.isPresent());
	}
}
