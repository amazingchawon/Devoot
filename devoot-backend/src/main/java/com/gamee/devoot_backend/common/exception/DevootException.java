package com.gamee.devoot_backend.common.exception;

import lombok.Getter;

@Getter
public class DevootException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String detail;

	public DevootException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.detail = null;
	}

	public DevootException(ErrorCode errorCode, String detail) {
		this.errorCode = errorCode;
		this.detail = detail;
	}
}
