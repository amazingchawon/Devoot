package com.gamee.devoot_backend.bookmark.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class BookmarkPermissionDeniedException extends DevootException {
	public BookmarkPermissionDeniedException() {
		super(BookmarkErrorCode.BOOKMARK_PERMISSION_DENIED);
	}

	public BookmarkPermissionDeniedException(String detail) {
		super(BookmarkErrorCode.BOOKMARK_PERMISSION_DENIED, detail);
	}
}
