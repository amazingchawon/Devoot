package com.gamee.devoot_backend.common.exception;

public class InvalidEnumException extends DevootException {
	public InvalidEnumException() {
		super(CommonErrorCode.INVALID_ENUM_EXCEPTION);
	}

	public InvalidEnumException(String detail) {
		super(CommonErrorCode.INVALID_ENUM_EXCEPTION, detail);
	}
}
