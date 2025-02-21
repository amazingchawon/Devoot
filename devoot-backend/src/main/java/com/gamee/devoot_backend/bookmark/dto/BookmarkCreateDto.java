package com.gamee.devoot_backend.bookmark.dto;

import jakarta.validation.constraints.NotNull;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;

public record BookmarkCreateDto(
	@NotNull(message = "Lecture ID must not be null")
	Long lectureId
) {
	public Bookmark toEntity() {
		return Bookmark.builder()
			.lectureId(lectureId)
			.status(1)
			.nextId(0L)
			.build();
	}
}
