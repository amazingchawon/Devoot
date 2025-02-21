package com.gamee.devoot_backend.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getStatus();

	String getCode();

	String getMessage();
}
