package com.gamee.devoot_backend.common.exception;

public class FileNameGenerationFailedException extends DevootException {
	public FileNameGenerationFailedException() {
		super(CommonErrorCode.FILE_NAME_GENERATION_FAILED);
	}
}
