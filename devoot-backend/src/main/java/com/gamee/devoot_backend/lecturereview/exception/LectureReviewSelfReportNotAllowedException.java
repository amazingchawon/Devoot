package com.gamee.devoot_backend.lecturereview.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureReviewSelfReportNotAllowedException extends DevootException {
	public LectureReviewSelfReportNotAllowedException() {
		super(LectureReviewErrorCode.SELF_REPORT_NOT_ALLOWED);
	}
}
