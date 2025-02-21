package com.gamee.devoot_backend.lecturereview.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public record LectureReviewDto(
	long id,
	long lectureId,
	long userId,
	float rating,
	String content,
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSS")
	LocalDateTime createdAt,
	String profileId,
	String nickname,
	String imageUrl,
	String reviewName,
	String sourceUrl,
	String sourceName
) {
	public LectureReviewDto(LectureReview review, String profileId, String nickname, String imageUrl, String reviewName, String sourceUrl, String sourceName) {
		this(review.getId(),
			review.getLecture().getId(),
			review.getUserId(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt(),
			profileId,
			nickname,
			imageUrl,
			reviewName,
			sourceUrl,
			sourceName
		);
	}
}
