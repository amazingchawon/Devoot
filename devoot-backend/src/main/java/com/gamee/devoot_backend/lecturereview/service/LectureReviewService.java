package com.gamee.devoot_backend.lecturereview.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.common.pageutils.PageSizeDefine;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.entity.LectureReviewReport;
import com.gamee.devoot_backend.lecturereview.exception.LectureAlreadyReviewedException;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewAlreadyReportedException;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewNotFoundException;
import com.gamee.devoot_backend.lecturereview.exception.LectureReviewSelfReportNotAllowedException;
import com.gamee.devoot_backend.lecturereview.exception.ReviewPermissionDeniedException;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewReportRepository;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;
import com.gamee.devoot_backend.user.service.UserService;

@Service
public class LectureReviewService {
	@Autowired
	private LectureReviewRepository lectureReviewRepository;
	@Autowired
	private LectureReviewReportRepository lectureReviewReportRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FollowRepository followRepository;
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private UserService userService;

	/**
	 * 강의에 대한 리뷰들을 가져온다.
	 * @param lectureId
	 * 		- 리뷰를 가져올 강의 id
	 * @param page
	 * 		- 리뷰를 가져올 페이지 번호
	 * @return
	 * 		- 리뷰, 페이지 정보
	 */
	public Page<LectureReviewDto> getLectureReviewList(long lectureId, int page) {
		Pageable pageable = PageRequest.of(page - 1, PageSizeDefine.REVIEW_LECTURE);
		return lectureReviewRepository.selectAllByLectureId(lectureId, pageable);
	}

	public Page<LectureReviewDto> getLectureReviewByProfileId(String profileId, int page, long currentUserId) {
		Pageable pageable = PageRequest.of(page - 1, PageSizeDefine.REVIEW_PROFILE);
		Optional<User> userOptional = userRepository.findByProfileId(profileId);
		long userId = -1;
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			userId = user.getId();
			if (userId != currentUserId
				&& !user.getIsPublic()
				&& followRepository.findIfAllowed(currentUserId, userId).isEmpty()) {
				throw new ReviewPermissionDeniedException();
			}
		}
		return lectureReviewRepository.selectAllByUserId(userId, pageable);
	}

	public LectureReviewDto getLectureReviewByIdAndLecture(CustomUserDetails userDetails, long lectureId) {
		if (userDetails == null) {
			return null;
		}
		Optional<LectureReview> reviewOptional = lectureReviewRepository.findByUserIdAndLectureId(userDetails.id(), lectureId);
		if (reviewOptional.isPresent()) {
			LectureReview review = reviewOptional.get();
			Lecture lecture = review.getLecture();
			return new LectureReviewDto(review, userDetails.profileId(), userDetails.nickname(), userDetails.imageUrl(), lecture.getName(), lecture.getSourceUrl(), lecture.getSourceName());
		}
		return null;
	}

	public void saveLectureReview(long userId, long lectureId, float rating, String content) {
		lectureRepository.findById(lectureId)
			.orElseThrow(LectureNotFoundException::new);

		lectureReviewRepository.findByUserIdAndLectureId(userId, lectureId)
			.ifPresent(review -> {
				throw new LectureAlreadyReviewedException();
			});

		LectureReview lectureReview = LectureReview.builder()
			.lectureId(lectureId)
			.userId(userId)
			.rating(rating)
			.content(content)
			.build();
		lectureReviewRepository.save(lectureReview);
		lectureRepository.incrementReviewStats(lectureId, rating);
	}

	public void updateLectureReview(long userId, long id, float rating, String content) {
		LectureReview review = checkUserIsAllowedAndFetchReview(userId, id);

		lectureRepository.updateReviewStats(review.getLectureId(), review.getRating(), rating);

		review.setRating(rating);
		review.setContent(content);
		lectureReviewRepository.save(review);
	}

	public void deleteLectureReview(long id, long userId) {
		Optional<LectureReview> reviewOptional = lectureReviewRepository.findById(id);
		LectureReview review;
		if (reviewOptional.isPresent()) {
			review = reviewOptional.get();
			if (review.getUserId() == userId) {
				lectureReviewReportRepository.deleteByLectureReviewId(id);
				lectureReviewRepository.deleteById(id);
				lectureRepository.decrementReviewStats(review.getLectureId(), review.getRating());
			} else {
				throw new ReviewPermissionDeniedException();
			}
		} else {
			throw new LectureReviewNotFoundException();
		}
	}

	public void reportLectureReview(Long userId, Long lectureReviewId) {
		LectureReview review = lectureReviewRepository.findById(lectureReviewId)
			.orElseThrow(() -> new LectureReviewNotFoundException());

		lectureReviewReportRepository.findByLectureReviewIdAndUserId(lectureReviewId, userId)
			.ifPresent(report -> {
				throw new LectureReviewAlreadyReportedException();
			});

		if (userId.equals(review.getUserId())) {
			throw new LectureReviewSelfReportNotAllowedException();
		}

		lectureReviewReportRepository.save(
			LectureReviewReport.builder()
				.userId(userId)
				.lectureReviewId(review.getId())
				.build()
		);
	}

	@Transactional
	public void deleteUserReviews(String profileId, CustomUserDetails userDetails) {
		userService.checkUserIsAdmin(userDetails.id());
		User user = userService.findUserByProfileId(profileId);
		Page<LectureReviewDto> reviews = lectureReviewRepository.selectAllByUserId(user.getId(), Pageable.ofSize(9999999));
		for (LectureReviewDto review : reviews.getContent()) {
			lectureRepository.decrementReviewStats(review.lectureId(), review.rating());
		}
		lectureReviewReportRepository.deleteByUserId(user.getId());
		lectureReviewRepository.deleteByUserId(user.getId());
	}

	@Transactional
	public void deleteReviewReportsOfReportedUser(String profileId, CustomUserDetails userDetails) {
		userService.checkUserIsAdmin(userDetails.id());
		User user = userService.findUserByProfileId(profileId);
		lectureReviewReportRepository.deleteByUserId(user.getId());
	}

	LectureReview checkUserIsAllowedAndFetchReview(Long userId, Long id) {
		LectureReview lectureReview = lectureReviewRepository.findById(id)
			.orElseThrow(LectureReviewNotFoundException::new);
		if (!userId.equals(lectureReview.getUserId())) {
			throw new ReviewPermissionDeniedException();
		}
		return lectureReview;
	}
}
