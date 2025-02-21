package com.gamee.devoot_backend.lecture.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureAlreadyReportedException extends DevootException {
	public LectureAlreadyReportedException() {
		super(LectureErrorCode.LECTURE_ALREADY_REPORTED);
	}
}
