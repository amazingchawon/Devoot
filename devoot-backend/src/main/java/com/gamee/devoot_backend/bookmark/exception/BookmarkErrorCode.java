package com.gamee.devoot_backend.bookmark.exception;

import org.springframework.http.HttpStatus;

import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookmarkErrorCode implements ErrorCode {
	BOOKMARK_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "BOOKMARK_403_1", "User is not authorized to perform this BOOKMARK operation"),
	BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK_404_1", "Bookmark not found"),
	DUPLICATE_BOOKMARK(HttpStatus.CONFLICT, "BOOKMARK_409_1", "User already bookmarked this lecture");
	private HttpStatus status;
	private String code;
	private String message;
}
