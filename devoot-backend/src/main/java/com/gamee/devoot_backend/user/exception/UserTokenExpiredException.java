package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserTokenExpiredException extends DevootException {
	public UserTokenExpiredException() {
		super(UserErrorCode.USER_TOKEN_EXPIRED);
	}
}
