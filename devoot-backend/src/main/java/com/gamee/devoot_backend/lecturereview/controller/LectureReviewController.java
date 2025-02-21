package com.gamee.devoot_backend.lecturereview.controller;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.exception.CommonErrorCode;
import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.dto.UpdateReviewDto;
import com.gamee.devoot_backend.lecturereview.service.LectureReviewService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

/**
 * 강의 리뷰와 관련된 요청
 */
@RestController
@RequestMapping("/api/reviews")
@Validated
public class LectureReviewController {
	@Autowired
	private LectureReviewService lectureReviewService;

	/**
	 * 특정 강의의 리뷰 목록을 반환
	 * @param lectureId
	 * - 리뷰를 가져올 강의 id
	 * @param page
	 * - 보여줄 리뷰 페이지 번호
	 * @return
	 * - 리뷰와 페이지 정보가 담긴 Page 객체
	 */
	@GetMapping("/lectures/{lectureId}")
	public ResponseEntity<CustomPage<LectureReviewDto>> getReviewListByLectureId(@PathVariable(value = "lectureId") Long lectureId, @RequestParam(value = "page", defaultValue = "1") int page) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewList(lectureId, page);
		return ResponseEntity.status(HttpStatus.OK).body(new CustomPage<>(lectureReviewDtoPage));
	}

	@GetMapping("/lectures/{lectureId}/my-review")
	public ResponseEntity<LectureReviewDto> getSelfReviewByLecture(@PathVariable(value = "lectureId") String lectureId,
		@AuthenticationPrincipal CustomUserDetails user) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(lectureReviewService.getLectureReviewByIdAndLecture(user, Long.parseLong(lectureId)));
		} catch (NumberFormatException e) {
			throw new DevootException(CommonErrorCode.VALIDATION_FAILED);
		}
	}

	@PostMapping
	@Transactional
	public ResponseEntity<Object> uploadReview(@RequestBody UpdateReviewDto reviewDto, @AuthenticationPrincipal CustomUserDetails user) {
		long userId = user.id();
		try {
			long lectureId = reviewDto.lectureId();
			float rating = reviewDto.rating();
			String content = reviewDto.content();
			lectureReviewService.saveLectureReview(userId, lectureId, rating, content);
			return ResponseEntity.ok().build();
		} catch (NumberFormatException e) {
			throw new DevootException(CommonErrorCode.VALIDATION_FAILED);
		}
	}

	@PatchMapping("/{reviewId}")
	@Transactional
	public ResponseEntity<Object> updateReview(@PathVariable("reviewId") String reviewId,
		@RequestBody UpdateReviewDto reviewDto,
		@AuthenticationPrincipal CustomUserDetails user) {
		long userId = user.id();
		try {
			lectureReviewService.updateLectureReview(userId, Long.parseLong(reviewId), reviewDto.rating(), reviewDto.content());
			return ResponseEntity.ok().build();
		} catch (NumberFormatException e) {
			throw new DevootException(CommonErrorCode.VALIDATION_FAILED);
		}
	}

	@DeleteMapping("/{reviewId}")
	@Transactional
	public ResponseEntity<Object> removeReview(@PathVariable("reviewId") String reviewId, @AuthenticationPrincipal CustomUserDetails user) {
		long userId = user.id();
		try {
			lectureReviewService.deleteLectureReview(Long.parseLong(reviewId), userId);
		} catch (NumberFormatException e) {
			throw new DevootException(CommonErrorCode.VALIDATION_FAILED);
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{reviewId}/report")
	@Transactional
	public ResponseEntity<Object> reportReview(@PathVariable("reviewId") Long reviewId, @AuthenticationPrincipal CustomUserDetails user) {
		lectureReviewService.reportLectureReview(user.id(), reviewId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/reports/of/{profileId}")
	@Transactional
	public ResponseEntity<?> removeReviewReportsOfReportedUser(
		@PathVariable String profileId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		lectureReviewService.deleteReviewReportsOfReportedUser(profileId, userDetails);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/of/{profileId}")
	@Transactional
	public ResponseEntity<?> removeReviewsOfUser(
		@PathVariable String profileId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		lectureReviewService.deleteUserReviews(profileId, userDetails);
		return ResponseEntity.noContent().build();
	}
}
