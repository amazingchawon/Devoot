package com.gamee.devoot_backend.lecturereview.controller;

import java.util.Optional;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gamee.devoot_backend.lecturereview.entity.LectureReview;
import com.gamee.devoot_backend.lecturereview.repository.LectureReviewRepository;

@SpringBootTest
@Transactional
class LectureReviewControllerTest {
	@Autowired
	LectureReviewRepository lectureReviewRepository;

	@Test
	public void saveTest() {
		LectureReview lectureReview = LectureReview.builder()
			.lectureId(1219)
			.userId(7)
			.rating(1.5f)
			.content("이 강의는 매우 형편없습니다!!")
			.build();
		lectureReview = lectureReviewRepository.save(lectureReview);
		System.out.println(lectureReview);
	}

	@Test
	public void modifyTest() {
		Optional<LectureReview> reviewOptional = lectureReviewRepository.findById(10L);
		LectureReview review;
		if (reviewOptional.isPresent()) {
			review = reviewOptional.get();
			System.out.println(review);
			review.setRating(5.0f);
			review.setContent("사실재밌는강의입니다..");
			review = lectureReviewRepository.save(review);
			System.out.println(review);
		}


	}
}
