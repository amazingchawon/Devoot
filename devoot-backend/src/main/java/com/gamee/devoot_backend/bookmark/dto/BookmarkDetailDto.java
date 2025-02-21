package com.gamee.devoot_backend.bookmark.dto;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;

import lombok.Builder;

@Builder
public record BookmarkDetailDto(
	Long id,
	Long userId,
	Integer status,
	Long nextId,
	Long lectureId
) {
	public static BookmarkDetailDto of(Bookmark bookmark) {
		return BookmarkDetailDto.builder()
			.id(bookmark.getId())
			.userId(bookmark.getUserId())
			.status(bookmark.getStatus())
			.nextId(bookmark.getNextId())
			.lectureId(bookmark.getLectureId())
			.build();
	}
}
