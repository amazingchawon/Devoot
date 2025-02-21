package com.gamee.devoot_backend.bookmark.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.bookmark.dto.BookmarkCreateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkDetailDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkUpdateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkWithLectureDetailDto;
import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.bookmark.exception.BookmarkNotFoundException;
import com.gamee.devoot_backend.bookmark.exception.BookmarkPermissionDeniedException;
import com.gamee.devoot_backend.bookmark.exception.DuplicateBookmarkException;
import com.gamee.devoot_backend.bookmark.repository.BookmarkLogRepository;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final BookmarkLogRepository bookmarkLogRepository;
	private final LectureRepository lectureRepository;
	private final UserService userService;
	private final FollowService followService;

	@Transactional
	public BookmarkDetailDto addBookmark(CustomUserDetails user, String profileId, BookmarkCreateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = dto.toEntity();
		bookmark.setUserId(user.id());

		lectureRepository.findById(bookmark.getLectureId())
			.orElseThrow(LectureNotFoundException::new);

		checkBookmarkExists(user, bookmark);

		bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), bookmark.getStatus(), 0L)
			.ifPresentOrElse(
				beforeBookmark -> {
					bookmarkRepository.save(bookmark);

					beforeBookmark.setNextId(bookmark.getId());
					bookmarkRepository.save(beforeBookmark);
				},
				() -> bookmarkRepository.save(bookmark)
			);

		bookmarkLogRepository.save(BookmarkLog.builder()
			.lectureId(bookmark.getLectureId())
			.bookmarkId(bookmark.getId())
			.userId(user.id())
			.beforeStatus(null)
			.afterStatus(bookmark.getStatus())
			.build());

		return BookmarkDetailDto.of(bookmark);
	}

	public Map<String, List<BookmarkWithLectureDetailDto>> getBookmarks(CustomUserDetails user, String profileId) {
		User followedUser = followService.validateAccessAndFetchFollowedUser(user, profileId);
		Map<String, List<Bookmark>> bookmarks = new LinkedHashMap<>();
		Map<Long, Bookmark> bookmarkMap = bookmarkRepository.findBookmarksByUserId(followedUser.getId()).stream()
			.collect(Collectors.toMap(Bookmark::getId, bookmark -> bookmark));

		bookmarks.put("todo", new ArrayList<>());
		bookmarks.put("in-progress", new ArrayList<>());
		bookmarks.put("done", new ArrayList<>());

		bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 1)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("todo"));
			});
		bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 2)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("in-progress"));
			});
		bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 3)
			.ifPresent(bookmark -> {
				addBookmarksInOrder(bookmark, bookmarkMap, bookmarks.get("done"));
			});

		return bookmarks.entrySet()
			.stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> entry.getValue().stream()
					.map(BookmarkWithLectureDetailDto::of)
					.collect(Collectors.toList())
			));
	}

	@Transactional
	public void updateBookmark(CustomUserDetails user, String profileId, Long bookmarkId, BookmarkUpdateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = checkUserIsAllowedAndFetchBookmark(user, bookmarkId);

		Integer beforeStatus = bookmark.getStatus();
		Integer newStatus = dto.status();
		Long beforeNextId = bookmark.getNextId();
		Long newNextId = dto.nextId();

		bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), beforeStatus, bookmark.getId())
			.ifPresent(beforeBookmark -> {
				beforeBookmark.setNextId(bookmark.getNextId());
				bookmarkRepository.save(beforeBookmark);
			});

		bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), newStatus, newNextId)
			.ifPresent(newBeforeBookmark -> {
				newBeforeBookmark.setNextId(bookmark.getId());
				bookmarkRepository.save(newBeforeBookmark);
			});

		bookmark.setNextId(newNextId);
		bookmark.setStatus(newStatus);
		bookmarkRepository.save(bookmark);

		if (beforeStatus != newStatus) {
			bookmarkLogRepository.save(BookmarkLog.builder()
				.lectureId(bookmark.getLectureId())
				.bookmarkId(bookmarkId)
				.userId(user.id())
				.beforeStatus(beforeStatus)
				.afterStatus(newStatus)
				.build());
		}
	}

	@Transactional
	public void deleteBookmark(CustomUserDetails user, String profileId, Long bookmarkId) {
		userService.checkUserMatchesProfileId(user, profileId);
		Bookmark bookmark = checkUserIsAllowedAndFetchBookmark(user, bookmarkId);

		bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), bookmark.getStatus(), bookmark.getId())
			.ifPresent(beforeBookmark -> {
				beforeBookmark.setNextId(bookmark.getNextId());
				bookmarkRepository.save(beforeBookmark);
			});
		bookmarkRepository.delete(bookmark);
	}

	private void addBookmarksInOrder(Bookmark startBookmark, Map<Long, Bookmark> bookmarkMap, List<Bookmark> bookmarks) {
		Bookmark currentBookmark = startBookmark;
		do {
			bookmarks.add(currentBookmark);
			currentBookmark = bookmarkMap.get(currentBookmark.getNextId());
		} while (currentBookmark != null);
	}

	private Bookmark checkUserIsAllowedAndFetchBookmark(CustomUserDetails user, Long bookmarkId) {
		Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
			.orElseThrow(BookmarkNotFoundException::new);
		if (!bookmark.getUserId().equals(user.id())) {
			throw new BookmarkPermissionDeniedException();
		}
		return bookmark;
	}

	private void checkBookmarkExists(CustomUserDetails user, Bookmark bookmark) {
		bookmarkRepository.findByUserIdAndLectureId(user.id(), bookmark.getLectureId())
			.ifPresent(existingBookmark -> {
				throw new DuplicateBookmarkException();
			});
	}
}
