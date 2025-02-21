package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowRelationshipNotFound extends DevootException {
	public FollowRelationshipNotFound() {
		super(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
	}
}
