package com.gamee.devoot_backend.lecturereview.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class LectureAlreadyReviewedException extends DevootException {
	public LectureAlreadyReviewedException() {
		super(LectureReviewErrorCode.USER_ALREADY_REVIEWD);
	}
}
