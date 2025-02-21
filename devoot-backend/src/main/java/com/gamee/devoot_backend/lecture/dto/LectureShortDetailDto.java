package com.gamee.devoot_backend.lecture.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamee.devoot_backend.common.Util;
import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureShortDetailDto(
	Long id,
	String name,
	String sourceName,
	String tags,
	String imageUrl,
	String sourceUrl,
	JsonNode curriculum
) {
	public static LectureShortDetailDto of(Lecture lecture) {
		return LectureShortDetailDto.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.sourceName(lecture.getSourceName())
			.imageUrl(lecture.getImageUrl())
			.tags(lecture.getTags())
			.curriculum(Util.parseToJson(lecture.getCurriculum()))
			.sourceUrl(lecture.getSourceUrl())
			.build();
	}
}
