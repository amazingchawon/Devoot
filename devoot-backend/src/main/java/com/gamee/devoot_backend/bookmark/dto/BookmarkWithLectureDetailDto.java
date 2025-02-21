package com.gamee.devoot_backend.bookmark.dto;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.lecture.dto.LectureShortDetailDto;

import lombok.Builder;

@Builder
public record BookmarkWithLectureDetailDto(
	Long id,
	Long userId,
	Integer status,
	Long nextId,
	LectureShortDetailDto lecture
) {
	public static BookmarkWithLectureDetailDto of(Bookmark bookmark) {
		return BookmarkWithLectureDetailDto.builder()
			.id(bookmark.getId())
			.userId(bookmark.getUserId())
			.lecture(LectureShortDetailDto.of(bookmark.getLecture()))
			.status(bookmark.getStatus())
			.nextId(bookmark.getNextId())
			.build();
	}
}
