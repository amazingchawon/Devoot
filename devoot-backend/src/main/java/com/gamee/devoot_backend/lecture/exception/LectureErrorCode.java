package com.gamee.devoot_backend.lecture.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LectureErrorCode implements ErrorCode {
	PERMISSION_DENIED(HttpStatus.FORBIDDEN, "REVIEW_403_1", "You do not have permission to access this resource."),
	PRIVATE_ACCOUNT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "LECTURE_403_1", "You do not have permission to access this user"),
	LECTURE_NOT_EXIST(HttpStatus.NOT_FOUND, "LECTURE_404_1", "Lecture does not exist."),
	LECTURE_CREATE_REQUEST_NOT_EXIST(HttpStatus.NOT_FOUND, "LECTURE_404_2", "Lecture create request does not exist."),
	LECTURE_UPDATE_REQUEST_NOT_EXIST(HttpStatus.NOT_FOUND, "LECTURE_404_3", "Lecture update request does not exist."),
	USER_ALREADY_REPORTED(HttpStatus.CONFLICT, "LECTURE_409_1", "User already reported this lecture"),
	SEARCH_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SEARCH_500_1", "Error occurred while executing search query.");
	private final HttpStatus status;
	private final String code;
	private final String message;
}
