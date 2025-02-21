package com.gamee.devoot_backend.lecture.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.lecture.entity.LectureCreateRequest;

import lombok.Builder;

@Builder
public record LectureCreateRequestDetailDto(
	Long id,
	String sourceUrl,
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {
	public static LectureCreateRequestDetailDto of(LectureCreateRequest lectureCreateRequest) {
		return LectureCreateRequestDetailDto.builder()
			.id(lectureCreateRequest.getId())
			.sourceUrl(lectureCreateRequest.getSourceUrl())
			.createdAt(lectureCreateRequest.getCreatedAt())
			.build();
	}
}
