package com.gamee.devoot_backend.bookmark.exception;

import com.gamee.devoot_backend.common.exception.DevootException;

public class BookmarkNotFoundException extends DevootException {
	public BookmarkNotFoundException() {
		super(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
	}

	public BookmarkNotFoundException(String detail) {
		super(BookmarkErrorCode.BOOKMARK_NOT_FOUND, detail);
	}
}
