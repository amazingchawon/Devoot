package com.gamee.devoot_backend.common.exception;

public class JsonParsingException extends DevootException {
	public JsonParsingException() {
		super(CommonErrorCode.JSON_PARSING_FAILED);
	}
}
