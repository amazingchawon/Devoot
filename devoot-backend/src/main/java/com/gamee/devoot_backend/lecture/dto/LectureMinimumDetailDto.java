package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureMinimumDetailDto(
	Long id,
	String name,
	String sourceName,
	String tags,
	String imageUrl
) {
	public static LectureMinimumDetailDto of(Lecture lecture) {
		return LectureMinimumDetailDto.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.tags(lecture.getTags())
			.build();
	}
}
