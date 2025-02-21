package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureUpdateRequestNotFoundException extends DevootException {
	public LectureUpdateRequestNotFoundException() {
		super(LectureErrorCode.LECTURE_UPDATE_REQUEST_NOT_EXIST);
	}
}
