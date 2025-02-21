package com.gamee.devoot_backend.todo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import com.gamee.devoot_backend.todo.entity.Todo;

public record TodoCreateDto(
	@NotNull(message = "Lecture ID must not be null")
	Long lectureId,

	@NotNull(message = "Date must not be null")
	LocalDate date,

	@NotNull(message = "Lecture name must not be null")
	@Size(min = 1, max = 100, message = "Lecture name must be between 1 and 100 characters")
	String lectureName,

	@NotNull(message = "Sub-lecture name must not be null")
	@Size(min = 1, max = 150, message = "Sub-lecture name must be between 1 and 150 characters")
	String subLectureName,

	@URL(message = "Source URL must be a valid URL")
	@NotNull(message = "Source URL must not be null")
	String sourceUrl,

	@NotNull(message = "Finished flag must not be null")
	Boolean finished
) {
	public Todo toEntity() {
		return Todo.builder()
			.lectureId(this.lectureId)
			.date(this.date)
			.lectureName(this.lectureName)
			.subLectureName(this.subLectureName)
			.sourceUrl(this.sourceUrl)
			.finished(this.finished)
			.nextId(0L)
			.build();
	}
}
