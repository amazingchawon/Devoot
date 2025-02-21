package com.gamee.devoot_backend.lecture.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gamee.devoot_backend.lecture.entity.LectureReport;

@DataJpaTest
public class LectureReportRepositoryTest {
	@Autowired
	private LectureReportRepository lectureReportRepository;

	@Test
	@DisplayName("Test findByLectureIdAndUserId()")
	public void testFindByLectureIdAndUserId() {
		// Given
		Long lectureId = 1L, userId = 2L;
		lectureReportRepository.save(
			LectureReport.builder()
				.lectureId(lectureId)
				.userId(userId)
				.build()
		);

		// When
		Optional<LectureReport> report = lectureReportRepository.findByLectureIdAndUserId(lectureId, userId);

		// Then
		assert (report.isPresent());
	}
}
