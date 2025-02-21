package com.gamee.devoot_backend.todo.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.todo.entity.TodoContribution;

import lombok.Builder;

@Builder
public record TodoContributionDetailDto(
	Integer cnt,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate date
) {
	public static TodoContributionDetailDto of(TodoContribution todoContribution) {
		return TodoContributionDetailDto.builder()
			.cnt(todoContribution.getCnt())
			.date(todoContribution.getDate())
			.build();
	}
}
