package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.lecture.entity.LectureUpdateRequest;

public record LectureUpdateRequestCreateDto(
	Long lectureId
) {
	public LectureUpdateRequest toEntity() {
		return LectureUpdateRequest.builder()
			.lectureId(lectureId)
			.build();
	}
}
