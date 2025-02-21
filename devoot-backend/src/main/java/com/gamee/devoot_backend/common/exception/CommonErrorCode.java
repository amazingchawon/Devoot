package com.gamee.devoot_backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON_400_1", "Invalid data provided"),
	JSON_PARSING_FAILED(HttpStatus.BAD_REQUEST, "COMMON_400_2", "There was an error parsing json data"),
	INVALID_ENUM_EXCEPTION(HttpStatus.BAD_REQUEST, "COMMON_400_2", "Given enum value is not valid"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_1", "Unexpected error in internal server has occurred."),
	S3_OPERATION_FAILED(HttpStatus.BAD_REQUEST, "S3_400_1", "S3 operation failed."),
	S3_FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "S3_400_2", "S3 file not found."),
	S3_UNEXPECTED_ERROR(HttpStatus.BAD_REQUEST, "S3_400_3", "Unexpected error occurred during S3 operation."),
	FILE_NAME_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_500_1", "Error generating unique file name.");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
