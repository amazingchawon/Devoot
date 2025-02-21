package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserNotFoundException extends DevootException {
	public UserNotFoundException() {
		super(UserErrorCode.USER_NOT_FOUND);
	}

	public UserNotFoundException(String message) {
		super(UserErrorCode.USER_NOT_FOUND, message);
	}
}
