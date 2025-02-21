package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureNotFoundException extends DevootException {
	public LectureNotFoundException(String detail) {
		super(LectureErrorCode.LECTURE_NOT_EXIST, detail);
	}

	public LectureNotFoundException() {
		super(LectureErrorCode.LECTURE_NOT_EXIST);
	}
}
