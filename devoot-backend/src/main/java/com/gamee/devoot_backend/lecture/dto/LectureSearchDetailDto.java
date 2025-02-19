package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureSearchDetailDto(
	Long id,
	String category,
	String tags,
	String name,
	String lecturer,
	Integer currentPrice,
	Integer originPrice,
	String sourceUrl,
	String sourceName,
	String imageUrl,
	Float rating,
	Integer reviewCnt
) {
	public static LectureSearchDetailDto of(Lecture lecture) {
		float ratingValue = (lecture.getReviewCnt() != null && lecture.getReviewCnt() > 0)
			? (lecture.getRatingSum() / lecture.getReviewCnt())
			: 0f;

		return LectureSearchDetailDto.builder()
			.id(lecture.getId())
			.category(lecture.getCategory())
			.tags(lecture.getTags())
			.name(lecture.getName())
			.lecturer(lecture.getLecturer())
			.currentPrice(lecture.getCurrentPrice())
			.originPrice(lecture.getOriginalPrice())
			.sourceUrl(lecture.getSourceUrl())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.rating(ratingValue)
			.reviewCnt(lecture.getReviewCnt())
			.build();
	}
}
