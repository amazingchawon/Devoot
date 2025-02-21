package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class SearchExecutionErrorException extends DevootException {
	public SearchExecutionErrorException() {
		super(LectureErrorCode.SEARCH_EXECUTION_ERROR);
	}
}
