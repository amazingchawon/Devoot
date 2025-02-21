package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureAlreadyRequestedException extends DevootException {
	public LectureAlreadyRequestedException() {
		super(LectureErrorCode.LECTURE_ALREADY_REQUESTED);
	}
}
