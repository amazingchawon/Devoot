package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowNotAuthorizedException extends DevootException {
	public FollowNotAuthorizedException() {
		super(FollowErrorCode.FOLLOW_REQUEST_NOT_AUTHORIZED);
	}
}
