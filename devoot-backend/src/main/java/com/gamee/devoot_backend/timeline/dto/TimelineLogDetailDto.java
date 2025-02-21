package com.gamee.devoot_backend.timeline.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.user.dto.UserShortDetailDto;

import lombok.Builder;

@Builder
public record TimelineLogDetailDto(
	Long id,
	String type,

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSS")
	LocalDateTime createdAt,
	UserShortDetailDto user,
	Object log
) {
	public static TimelineLogDetailDto of(TimelineLog timelineLog) {
		String type = null;
		if (timelineLog instanceof BookmarkLog) {
			type = "BOOKMARK";
		} else if (timelineLog instanceof TodoLog) {
			type = "TODO";
		}

		return TimelineLogDetailDto.builder()
			.id(timelineLog.getId())
			.user(UserShortDetailDto.of(timelineLog.getUser()))
			.createdAt(timelineLog.getCreatedAt())
			.type(type)
			.log(timelineLog.getLogData())
			.build();
	}
}
