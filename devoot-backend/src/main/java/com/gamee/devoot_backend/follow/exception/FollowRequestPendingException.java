package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowRequestPendingException extends DevootException {
	public FollowRequestPendingException() {
		super(FollowErrorCode.FOLLOW_REQUEST_PENDING);
	}

	public FollowRequestPendingException(String detail) {
		super(FollowErrorCode.FOLLOW_REQUEST_PENDING, detail);
	}
}
