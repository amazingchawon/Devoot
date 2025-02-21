package com.gamee.devoot_backend.lecturereview.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class ReviewPermissionDeniedException extends DevootException {
	public ReviewPermissionDeniedException() {
		super(LectureReviewErrorCode.PERMISSION_DENIED);
	}

	public ReviewPermissionDeniedException(String detail) {
		super(LectureReviewErrorCode.PERMISSION_DENIED, detail);
	}
}
