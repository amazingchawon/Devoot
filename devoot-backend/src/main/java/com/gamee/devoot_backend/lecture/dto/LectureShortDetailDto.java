package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureShortDetailDto(
	Long id,
	String name,
	String sourceName,
	String tags,
	String imageUrl,
	String curriculum
) {
	public static LectureShortDetailDto of(Lecture lecture) {
		return LectureShortDetailDto.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.tags(lecture.getTags())
			.curriculum(lecture.getCurriculum())
			.build();
	}
}
