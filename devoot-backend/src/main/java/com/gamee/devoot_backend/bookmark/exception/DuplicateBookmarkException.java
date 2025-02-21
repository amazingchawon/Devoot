package com.gamee.devoot_backend.bookmark.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class DuplicateBookmarkException extends DevootException {
	public DuplicateBookmarkException() {
		super(BookmarkErrorCode.DUPLICATE_BOOKMARK);
	}

	public DuplicateBookmarkException(String detail) {
		super(BookmarkErrorCode.DUPLICATE_BOOKMARK, detail);
	}
}
