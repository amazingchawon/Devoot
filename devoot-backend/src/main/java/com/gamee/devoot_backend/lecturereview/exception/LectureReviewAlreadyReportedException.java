package com.gamee.devoot_backend.lecturereview.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureReviewAlreadyReportedException extends DevootException {
	public LectureReviewAlreadyReportedException() {
		super(LectureReviewReportErrorCode.USER_ALREADY_REPORTED);
	}
}
