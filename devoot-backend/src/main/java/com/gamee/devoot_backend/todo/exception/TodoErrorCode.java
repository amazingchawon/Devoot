package com.gamee.devoot_backend.todo.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoErrorCode implements ErrorCode {
	TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO_404_1", "Todo not found"),
	TODO_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "TODO_403_1", "User is not authorized to perform this TODO operation");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
