package com.gamee.devoot_backend.user.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "USER_401_1", "Firebase token has expired. Please reauthenticate."),
	USER_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "USER_401_2", "Invalid Firebase token. Please provide a valid token."),
	USER_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "USER_401_3", "Firebase authentication failed."),
	USER_PROFILE_ID_MISMATCH(HttpStatus.FORBIDDEN, "USER_403_1", "Requested user doesn't match profile id"),
	USER_NOT_ADMIN(HttpStatus.FORBIDDEN, "USER_403_2", "User doesn't have admin privileges required for this operation"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "User not found"),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_1", "User already exists"),
	USER_PROFILE_ID_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_409_2", "User profileId already exists");


	private final HttpStatus status;
	private final String code;
	private final String message;

	public static UserErrorCode fromFirebaseErrorCode(String errorCode) {
		if (errorCode == null) {
			return USER_AUTHENTICATION_FAILED;
		}
		return switch (errorCode) {
			case "ID_TOKEN_EXPIRED" -> USER_TOKEN_EXPIRED;
			case "INVALID_ID_TOKEN" -> USER_INVALID_TOKEN;
			default -> USER_AUTHENTICATION_FAILED;
		};
	}
}
