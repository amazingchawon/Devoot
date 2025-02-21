package com.gamee.devoot_backend.lecture.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamee.devoot_backend.common.Util;
import com.gamee.devoot_backend.lecture.entity.Lecture;

import lombok.Builder;

@Builder
public record LectureDetailDto(
	Long id,
	String category,
	String tags,
	String name,
	String lecturer,
	int currentPrice,
	int originPrice,
	String sourceName,
	String sourceUrl,
	String imgUrl,
	JsonNode curriculum
) {
	public static LectureDetailDto of(Lecture lecture) {
		return LectureDetailDto.builder()
			.id(lecture.getId())
			.category(lecture.getCategory())
			.tags(lecture.getTags())
			.name(lecture.getName())
			.lecturer(lecture.getLecturer())
			.currentPrice(lecture.getCurrentPrice())
			.originPrice(lecture.getOriginalPrice())
			.sourceName(lecture.getSourceName())
			.sourceUrl(lecture.getSourceUrl())
			.imgUrl(lecture.getImageUrl())
			.curriculum(Util.parseToJson(lecture.getCurriculum()))
			.build();
	}
}
