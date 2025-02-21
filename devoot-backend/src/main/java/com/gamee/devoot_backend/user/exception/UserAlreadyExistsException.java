package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserAlreadyExistsException extends DevootException {
	public UserAlreadyExistsException() {
		super(UserErrorCode.USER_ALREADY_EXISTS);
	}
}
