package com.gamee.devoot_backend.lecture.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamee.devoot_backend.lecture.entity.Lecture;

public record LectureUpdateDto(
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
	public void updateEntity(Lecture lecture) {
		lecture.setCategory(category);
		lecture.setTags(tags);
		lecture.setName(name);
		lecture.setLecturer(lecturer);
		lecture.setCurrentPrice(currentPrice);
		lecture.setOriginalPrice(originPrice);
		lecture.setSourceName(sourceName);
		lecture.setSourceUrl(sourceUrl);
		lecture.setImageUrl(imgUrl);
		lecture.setCurriculum(curriculum.toString());
	}
}
