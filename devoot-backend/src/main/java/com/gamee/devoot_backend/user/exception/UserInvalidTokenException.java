package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserInvalidTokenException extends DevootException {
	public UserInvalidTokenException() {
		super(UserErrorCode.USER_INVALID_TOKEN);
	}
}
