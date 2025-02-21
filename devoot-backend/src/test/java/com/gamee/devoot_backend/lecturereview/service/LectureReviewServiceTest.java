package com.gamee.devoot_backend.lecturereview.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.entity.LectureReviewReport;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewAlreadyReportedException;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewNotFoundException;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewSelfReportNotAllowedException;
import com.gamee.devoot_backend.lecturereview.exception.ReviewPermissionDeniedException;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewReportRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

@ExtendWith(MockitoExtension.class)
public class LectureReviewServiceTest {
	@Mock
	LectureReviewRepository lectureReviewRepository;

	@Mock
	LectureReviewReportRepository lectureReviewReportRepository;

	@Mock
	LectureRepository lectureRepository;

	@InjectMocks
	LectureReviewService lectureReviewService;

	@Test
	@DisplayName("Test saveLectureReview() - successful")
	public void testSaveLectureReview1() {
		// Given
		Long userId = 1L, lectureId = 2L;
		Float rating = 3.0f;
		String content = "";

		Lecture lecture = Lecture.builder().ratingSum(5.0f).reviewCnt(1).build();
		when(lectureRepository.findById(lectureId))
			.thenReturn(Optional.of(lecture));

		// When
		lectureReviewService.saveLectureReview(userId, lectureId, rating, content);

		// Then
		verify(lectureReviewRepository).save(any());
		verify(lectureRepository).incrementReviewStats(lectureId, rating);
	}

	@Test
	@DisplayName("Test checkUserIsAllowedAndFetchReview() - successful")
	public void testCheckUserIsAllowedAndFetchReview1() {
		// Given
		Long userId = 1L, reviewId = 2L, lectureId = 3L;

		LectureReview lectureReview = LectureReview.builder().id(reviewId).lectureId(lectureId).userId(userId).build();
		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(lectureReview));

		// When & Then
		assertDoesNotThrow(() -> lectureReviewService.deleteLectureReview(reviewId, userId));
	}

	@Test
	@DisplayName("Test checkUserIsAllowedAndFetchReview() - throws LectureReviewNotFoundException")
	public void testCheckUserIsAllowedAndFetchReview2() {
		// Given
		Long userId = 1L, reviewId = 2L, lectureId = 3L;

		LectureReview lectureReview = LectureReview.builder().id(reviewId).lectureId(lectureId).userId(userId).build();
		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThrows(LectureReviewNotFoundException.class,
			() -> lectureReviewService.deleteLectureReview(reviewId, userId));
	}

	@Test
	@DisplayName("Test checkUserIsAllowedAndFetchReview() - throws ReviewPermissionDeniedException")
	public void testCheckUserIsAllowedAndFetchReview3() {
		// Given
		Long userId = 1L, diffUserId = 2L, reviewId = 2L, lectureId = 3L;

		LectureReview lectureReview = LectureReview.builder().id(reviewId).lectureId(lectureId).userId(userId).build();
		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(lectureReview));

		// When & Then
		assertThrows(ReviewPermissionDeniedException.class,
			() -> lectureReviewService.deleteLectureReview(reviewId, diffUserId));
	}

	@Test
	@DisplayName("Test updateLectureReview() - successful")
	public void testUpdateLectureReview1() {
		// Given
		Long userId = 1L, reviewId = 2L, lectureId = 3L;
		Float beforeRating = 3.0f, newRating = 4.0f;
		String content = "";

		LectureReview lectureReview = LectureReview.builder().id(reviewId).lectureId(lectureId).rating(beforeRating).userId(userId).build();
		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(lectureReview));

		// When
		lectureReviewService.updateLectureReview(userId, reviewId, newRating, content);

		// Then
		verify(lectureReviewRepository).save(any());
		verify(lectureRepository).updateReviewStats(lectureId, beforeRating, newRating);
	}

	@Test
	@DisplayName("Test deleteLectureReview() - successful")
	public void deleteLectureReview1() {
		// Given
		Long userId = 1L, reviewId = 2L, lectureId = 3L;
		Float rating = 4.0f;

		LectureReview lectureReview = LectureReview.builder().id(reviewId).lectureId(lectureId).rating(rating).userId(userId).build();
		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(lectureReview));

		// When
		lectureReviewService.deleteLectureReview(reviewId, userId);

		// Then
		verify(lectureReviewRepository).deleteById(reviewId);
		verify(lectureRepository).decrementReviewStats(lectureId, rating);
	}

	@Test
	@DisplayName("Test reportLectureReview() - throw LectureNotFoundException")
	public void testReportLectureReview1() {
		// Given
		Long userId = 1L, lectureId = 3L;

		// When
		assertThrows(LectureReviewNotFoundException.class,
			() -> lectureReviewService.reportLectureReview(userId, lectureId));
	}

	@Test
	@DisplayName("Test reportLectureReview() - throw LectureReviewAlreadyReportedException")
	public void testReportLectureReview2() {
		// Given
		Long userId = 1L, reviewId = 2L;

		LectureReview review = LectureReview.builder().id(reviewId).build();
		LectureReviewReport report = LectureReviewReport.builder().lectureReviewId(reviewId).userId(userId).build();

		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(review));
		when(lectureReviewReportRepository.findByLectureReviewIdAndUserId(reviewId, userId))
			.thenReturn(Optional.of(report));

		// When
		assertThrows(LectureReviewAlreadyReportedException.class,
			() -> lectureReviewService.reportLectureReview(userId, reviewId));
	}

	@Test
	@DisplayName("Test reportLectureReview() - throw LectureReviewSelfReportNotAllowedException")
	public void testReportLectureReview3() {
		// Given
		Long userId = 1L, reviewId = 2L;

		LectureReview review = LectureReview.builder().id(reviewId).userId(userId).build();

		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(review));
		when(lectureReviewReportRepository.findByLectureReviewIdAndUserId(reviewId, userId))
			.thenReturn(Optional.empty());

		// When
		assertThrows(LectureReviewSelfReportNotAllowedException.class,
			() -> lectureReviewService.reportLectureReview(userId, reviewId));
	}

	@Test
	@DisplayName("Test reportLectureReview() - Successful")
	public void testReportLectureReview4() {
		// Given
		Long userId = 1L, diffUserId = 3L, reviewId = 2L;

		LectureReview review = LectureReview.builder().id(reviewId).userId(diffUserId).build();

		when(lectureReviewRepository.findById(reviewId))
			.thenReturn(Optional.of(review));
		when(lectureReviewReportRepository.findByLectureReviewIdAndUserId(reviewId, userId))
			.thenReturn(Optional.empty());

		// When
		lectureReviewService.reportLectureReview(userId, reviewId);

		// Then
		verify(lectureReviewReportRepository).save(any());
	}
}
