package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class DuplicateLectureException extends DevootException {
	public DuplicateLectureException() {
		super(LectureErrorCode.DUPLICATE_LECTURE);
	}
}
