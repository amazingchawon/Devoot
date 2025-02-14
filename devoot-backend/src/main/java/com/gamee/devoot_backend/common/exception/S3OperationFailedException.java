package com.gamee.devoot_backend.common.exception;

public class S3OperationFailedException extends DevootException {
	public S3OperationFailedException() {
		super(CommonErrorCode.S3_OPERATION_FAILED);
	}

	public S3OperationFailedException(String detail) {
		super(CommonErrorCode.S3_OPERATION_FAILED, detail);
	}
}
