package com.gamee.devoot_backend.todo.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class TodoNotFoundException extends DevootException {
	public TodoNotFoundException(String detail) {
		super(TodoErrorCode.TODO_NOT_FOUND, detail);
	}

	public TodoNotFoundException() {
		super(TodoErrorCode.TODO_NOT_FOUND);
	}
}
