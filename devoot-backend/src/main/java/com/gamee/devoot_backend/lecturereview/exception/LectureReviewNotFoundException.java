package com.gamee.devoot_backend.lecturereview.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureReviewNotFoundException extends DevootException {
	public LectureReviewNotFoundException() {
		super(LectureReviewErrorCode.LECTURE_REVIEW_NOT_FOUND);
	}

	public LectureReviewNotFoundException(String detail) {
		super(LectureReviewErrorCode.LECTURE_REVIEW_NOT_FOUND, detail);
	}
}
