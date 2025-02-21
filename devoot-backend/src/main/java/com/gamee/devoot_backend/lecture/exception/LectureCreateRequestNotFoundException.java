package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureCreateRequestNotFoundException extends DevootException {
	public LectureCreateRequestNotFoundException() {
		super(LectureErrorCode.LECTURE_CREATE_REQUEST_NOT_EXIST);
	}
}
