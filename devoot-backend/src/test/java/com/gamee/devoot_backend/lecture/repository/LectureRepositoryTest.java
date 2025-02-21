package com.gamee.devoot_backend.lecture.repository;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.gamee.devoot_backend.lecture.entity.Lecture;

@DataJpaTest
public class LectureRepositoryTest {
	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Test incrementReviewStats()")
	public void testIncrementReviewStats() {
		// Given
		Lecture lecture = Lecture.builder().ratingSum(0.0f).reviewCnt(0).build();
		lectureRepository.save(lecture);

		// When
		lectureRepository.incrementReviewStats(lecture.getId(), 5.0f);
		em.flush();
		em.clear();

		// Then
		lecture = lectureRepository.findById(lecture.getId()).get();
		assertEquals(5.0f, lecture.getRatingSum());
		assertEquals(1, lecture.getReviewCnt());
	}

	@Test
	@DisplayName("Test decrementReviewStats()")
	public void testDecrementReviewStats() {
		// Given
		Lecture lecture = Lecture.builder().ratingSum(5.0f).reviewCnt(1).build();
		lectureRepository.save(lecture);

		// When
		lectureRepository.decrementReviewStats(lecture.getId(), lecture.getRatingSum());
		em.flush();
		em.clear();

		// Then
		lecture = lectureRepository.findById(lecture.getId()).get();
		assertEquals(0f, lecture.getRatingSum());
		assertEquals(0, lecture.getReviewCnt());
	}

	@Test
	@DisplayName("Test incrementReviewStats()")
	public void updateReviewStats() {
		// Given
		Lecture lecture = Lecture.builder().ratingSum(5.0f).reviewCnt(1).build();
		lectureRepository.save(lecture);

		// When
		lectureRepository.updateReviewStats(lecture.getId(), 5.0f, 3.0f);
		em.flush();
		em.clear();

		// Then
		lecture = lectureRepository.findById(lecture.getId()).get();
		assertEquals(lecture.getRatingSum(), 3.0f);
		assertEquals(lecture.getReviewCnt(), 1);
	}
}
