package com.gamee.devoot_backend.lecture.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.lecture.entity.LectureUpdateRequest;

import lombok.Builder;

@Builder
public record LectureUpdateRequestDetailDto(
	Long id,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt,
	LectureDetailDto lecture
) {
	public static LectureUpdateRequestDetailDto of(LectureUpdateRequest request) {
		return LectureUpdateRequestDetailDto.builder()
			.id(request.getId())
			.createdAt(request.getCreatedAt())
			.lecture(LectureDetailDto.of(request.getLecture()))
			.build();
	}
}
