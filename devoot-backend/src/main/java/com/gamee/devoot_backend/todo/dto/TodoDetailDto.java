package com.gamee.devoot_backend.todo.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.todo.entity.Todo;

import lombok.Builder;

@Builder
public record TodoDetailDto(
	Long id,
	Long userId,
	Long lectureId,

	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate date,
	String lectureName,
	String subLectureName,
	String sourceUrl,
	Boolean finished,
	Long nextId
) {
	public static TodoDetailDto of(Todo todo) {
		return TodoDetailDto.builder()
			.id(todo.getId())
			.userId(todo.getUserId())
			.lectureId(todo.getLectureId())
			.date(todo.getDate())
			.lectureName(todo.getLectureName())
			.subLectureName(todo.getSubLectureName())
			.sourceUrl(todo.getSourceUrl())
			.finished(todo.getFinished())
			.nextId(todo.getNextId())
			.build();
	}
}
