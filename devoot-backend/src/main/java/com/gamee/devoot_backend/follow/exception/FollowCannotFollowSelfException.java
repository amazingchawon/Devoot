package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowCannotFollowSelfException extends DevootException {
	public FollowCannotFollowSelfException() {
		super(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF);
	}
}
