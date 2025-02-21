package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserProfileIdAlreadyExistsException extends DevootException {
	public UserProfileIdAlreadyExistsException() {
		super(UserErrorCode.USER_PROFILE_ID_ALREADY_EXISTS);
	}
}
