package com.gamee.devoot_backend.todo.dto;

import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;

import lombok.Builder;

@Builder
public record TodoLogDetailDto(
	TodoDetailDto todo
) {
	public static TodoLogDetailDto of(TodoLog todoLog) {
		return TodoLogDetailDto.builder()
			.todo(TodoDetailDto.of(todoLog.getTodo()))
			.build();
	}
}
