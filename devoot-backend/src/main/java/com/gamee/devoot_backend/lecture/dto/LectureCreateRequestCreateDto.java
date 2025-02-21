package com.gamee.devoot_backend.lecture.dto;

import com.gamee.devoot_backend.common.Util;
import com.gamee.devoot_backend.lecture.entity.LectureCreateRequest;

public record LectureCreateRequestCreateDto(
	String sourceUrl
) {
	public LectureCreateRequest toEntity() {
		return LectureCreateRequest.builder()
			.sourceUrl(sourceUrl)
			.hash(Util.sha256(sourceUrl))
			.build();
	}
}
