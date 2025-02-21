package com.gamee.devoot_backend.follow.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class FollowRelationshipAlreadyExists extends DevootException {
	public FollowRelationshipAlreadyExists() {
		super(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS);
	}
}
