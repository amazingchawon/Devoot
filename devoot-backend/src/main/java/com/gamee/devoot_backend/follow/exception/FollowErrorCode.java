package com.gamee.devoot_backend.follow.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {
	FOLLOW_RELATIONSHIP_ALREADY_EXISTS(HttpStatus.CONFLICT, "FOLLOW_409_1", "Already following this user."),
	FOLLOW_REQUEST_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "FOLLOW_409_2", "This follow request has already been accepted."),
	FOLLOW_CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "FOLLOW_400_1", "Can't follow yourself."),
	FOLLOW_RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_404_1", "Follow relationship does not exist."),
	FOLLOW_REQUEST_PENDING(HttpStatus.FORBIDDEN, "FOLLOW_403_1", "User's follow request is still pending approval"),
	FOLLOW_REQUEST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "FOLLOW_403_2", "Not authorized to accept this follow request");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
