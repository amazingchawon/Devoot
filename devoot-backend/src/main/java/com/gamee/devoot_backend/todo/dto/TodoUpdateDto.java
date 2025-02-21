package com.gamee.devoot_backend.todo.dto;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import com.gamee.devoot_backend.todo.entity.Todo;

public record TodoUpdateDto(
	Boolean finished,

	@NotNull(message = "NextId must not be null")
	Long nextId
) {
	public Todo toEntity() {
		Todo todo = new Todo();

		Optional.ofNullable(finished).ifPresent(todo::setFinished);
		todo.setNextId(nextId);

		return todo;
	}
}
