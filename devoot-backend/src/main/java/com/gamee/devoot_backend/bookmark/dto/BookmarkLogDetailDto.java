package com.gamee.devoot_backend.bookmark.dto;

import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.lecture.dto.LectureMinimumDetailDto;

import lombok.Builder;

@Builder
public record BookmarkLogDetailDto(
	Integer beforeStatus,
	Integer afterStatus,
	LectureMinimumDetailDto lecture
) {
	public static BookmarkLogDetailDto of(BookmarkLog bookmarkLog) {
		return BookmarkLogDetailDto.builder()
			.beforeStatus(bookmarkLog.getBeforeStatus())
			.afterStatus(bookmarkLog.getAfterStatus())
			.lecture(LectureMinimumDetailDto.of(bookmarkLog.getLecture()))
			.build();
	}
}
