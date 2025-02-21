package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowRequestAlreadyAcceptedException extends DevootException {
	public FollowRequestAlreadyAcceptedException() {
		super(FollowErrorCode.FOLLOW_REQUEST_ALREADY_ACCEPTED);
	}
}
