package com.gamee.devoot_backend.todo.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class TodoPermissionDeniedException extends DevootException {
	public TodoPermissionDeniedException(String detail) {
		super(TodoErrorCode.TODO_PERMISSION_DENIED, detail);
	}

	public TodoPermissionDeniedException() {
		super(TodoErrorCode.TODO_PERMISSION_DENIED);
	}
}
