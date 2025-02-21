package com.gamee.devoot_backend.todo.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;
import com.gamee.devoot_backend.todo.dto.TodoLogDetailDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todolog")
@DiscriminatorValue("TODO")
@EqualsAndHashCode(callSuper = true)
public class TodoLog extends TimelineLog {
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "todoId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Todo todo;
	private Long todoId;

	@JsonProperty("log")
	public TodoLogDetailDto getLogData() {
		return TodoLogDetailDto.of(this);
	}
}
