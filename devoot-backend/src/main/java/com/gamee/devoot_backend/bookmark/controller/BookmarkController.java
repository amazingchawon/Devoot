package com.gamee.devoot_backend.bookmark.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.bookmark.dto.BookmarkCreateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkDetailDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkUpdateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkWithLectureDetailDto;
import com.gamee.devoot_backend.bookmark.service.BookmarkService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/{profileId}/bookmarks")
@RequiredArgsConstructor
@Validated
public class BookmarkController {
	private final BookmarkService bookmarkService;

	@PostMapping
	public ResponseEntity<?> addBookmark(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@RequestBody @Valid BookmarkCreateDto dto) {
		BookmarkDetailDto bookmark = bookmarkService.addBookmark(user, profileId, dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
	}

	@GetMapping
	public ResponseEntity<?> getBookmarks(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId) {
		Map<String, List<BookmarkWithLectureDetailDto>> bookmarks = bookmarkService.getBookmarks(user, profileId);
		return ResponseEntity.status(HttpStatus.OK).body(bookmarks);
	}

	@DeleteMapping("/{bookmarkId}")
	public ResponseEntity<?> deleteBookmark(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@PathVariable Long bookmarkId) {
		bookmarkService.deleteBookmark(user, profileId, bookmarkId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{bookmarkId}")
	public ResponseEntity<?> updateBookmark(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@PathVariable Long bookmarkId,
		@RequestBody @Valid BookmarkUpdateDto dto) {
		bookmarkService.updateBookmark(user, profileId, bookmarkId, dto);
		return ResponseEntity.noContent().build();
	}
}
