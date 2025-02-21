package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserNotAdminException extends DevootException {
	public UserNotAdminException() {
		super(UserErrorCode.USER_NOT_ADMIN);
	}
}
