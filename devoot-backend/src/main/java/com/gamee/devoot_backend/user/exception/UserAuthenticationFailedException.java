package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserAuthenticationFailedException extends DevootException {
	public UserAuthenticationFailedException() {
		super(UserErrorCode.USER_AUTHENTICATION_FAILED);
	}
}
