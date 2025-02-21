package com.gamee.devoot_backend.bookmark.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;

public record BookmarkUpdateDto(
	@Min(value = 1, message = "status must be integer value of 1 ~ 3")
	@Max(value = 3, message = "status must be integer value of 1 ~ 3")
	Integer status,
	Long nextId
) {
	public Bookmark toEntity() {
		return Bookmark.builder()
			.status(status)
			.nextId(nextId)
			.build();
	}
}
