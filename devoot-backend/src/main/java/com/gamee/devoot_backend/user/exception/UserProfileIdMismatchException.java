package com.gamee.devoot_backend.user.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class UserProfileIdMismatchException extends DevootException {
	public UserProfileIdMismatchException() {
		super(UserErrorCode.USER_PROFILE_ID_MISMATCH);
	}
}
