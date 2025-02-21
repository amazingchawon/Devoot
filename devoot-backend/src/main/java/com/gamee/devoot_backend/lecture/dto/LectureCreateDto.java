package com.gamee.devoot_backend.lecture.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamee.devoot_backend.common.Util;
import com.gamee.devoot_backend.lecture.entity.Lecture;

public record LectureCreateDto(
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
	public Lecture toEntity() {
		return Lecture.builder()
			.category(category)
			.tags(tags)
			.name(name)
			.lecturer(lecturer)
			.currentPrice(currentPrice)
			.originalPrice(originPrice)
			.sourceName(sourceName)
			.sourceUrl(sourceUrl)
			.imageUrl(imgUrl)
			.curriculum(curriculum.toString())
			.ratingSum(0f)
			.reviewCnt(0)
			.hash(Util.sha256(sourceUrl))
			.build();
	}
}
