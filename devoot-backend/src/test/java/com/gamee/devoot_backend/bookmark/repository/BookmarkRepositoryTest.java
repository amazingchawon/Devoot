package com.gamee.devoot_backend.bookmark.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;

@DataJpaTest
@ActiveProfiles("test")
public class BookmarkRepositoryTest {
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Test findFirstBookmarkOf()")
	public void testFindFirstBookmarkOf() {
		// Given
		Long userId = 1L;
		Long lectureId = 1L;

		Bookmark bookmark1 = Bookmark.builder()
			.userId(userId)
			.lectureId(lectureId)
			.status(1)
			.build();
		Bookmark bookmark2 = Bookmark.builder()
			.userId(userId)
			.lectureId(lectureId + 1)
			.status(1)
			.build();
		Bookmark bookmark3 = Bookmark.builder()
			.userId(userId)
			.lectureId(lectureId + 2)
			.status(1)
			.nextId(0L)
			.build();
		Bookmark bookmark4 = Bookmark.builder()
			.userId(userId)
			.lectureId(lectureId + 3)
			.status(2)
			.nextId(0L)
			.build();

		bookmarkRepository.save(bookmark1);
		bookmarkRepository.save(bookmark2);
		bookmarkRepository.save(bookmark3);
		bookmarkRepository.save(bookmark4);
		bookmark1.setNextId(bookmark2.getId());
		bookmark2.setNextId(bookmark3.getId());
		bookmarkRepository.save(bookmark1);
		bookmarkRepository.save(bookmark2);

		// When

		Optional<Bookmark> bookmarkOptional = bookmarkRepository.findFirstBookmarkOf(userId, 1);

		// Then
		assertTrue(bookmarkOptional.isPresent());
		assertEquals(bookmark1.getId(), bookmarkOptional.get().getId());
	}

	@Test
	@DisplayName("Test findBookmarksByUserId()")
	public void testFindBookmarksByUserId() {
		// Given
		Lecture lecture1 = Lecture.builder().build();
		Lecture lecture2 = Lecture.builder().build();
		Lecture lecture3 = Lecture.builder().build();
		Lecture lecture4 = Lecture.builder().build();
		lectureRepository.saveAndFlush(lecture1);
		lectureRepository.saveAndFlush(lecture2);
		lectureRepository.saveAndFlush(lecture3);
		lectureRepository.saveAndFlush(lecture4);

		Long userId = 1L;

		Bookmark bookmark1 = Bookmark.builder()
			.userId(userId)
			.lectureId(lecture1.getId())
			.status(1)
			.build();
		Bookmark bookmark2 = Bookmark.builder()
			.userId(userId)
			.lectureId(lecture2.getId())
			.status(1)
			.build();
		Bookmark bookmark3 = Bookmark.builder()
			.userId(userId + 1)
			.lectureId(lecture3.getId())
			.status(1)
			.nextId(0L)
			.build();
		Bookmark bookmark4 = Bookmark.builder()
			.userId(userId + 2)
			.lectureId(lecture4.getId())
			.status(2)
			.nextId(0L)
			.build();

		bookmarkRepository.saveAndFlush(bookmark1);
		bookmarkRepository.saveAndFlush(bookmark2);
		bookmarkRepository.saveAndFlush(bookmark3);
		bookmarkRepository.saveAndFlush(bookmark4);

		em.clear();

		// When
		List<Bookmark> bookmarks = bookmarkRepository.findBookmarksByUserId(userId);

		// Then
		assertEquals(2, bookmarks.size());
		for (Bookmark bookmark : bookmarks) {
			assertNotNull(bookmark.getLecture());
		}
	}

	@Test
	@DisplayName("Test findByUserIdAndNextId()")
	public void testFindByUserIdAndNextId() {
		Long userId = 1L;

		Bookmark bookmark1 = Bookmark.builder()
			.userId(userId)
			.lectureId(1L)
			.status(1)
			.build();
		Bookmark bookmark2 = Bookmark.builder()
			.userId(userId)
			.lectureId(2L)
			.status(1)
			.build();
		bookmarkRepository.save(bookmark1);
		bookmarkRepository.save(bookmark2);
		bookmark2.setNextId(bookmark1.getId());
		bookmarkRepository.save(bookmark2);

		// When
		Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUserIdAndNextId(userId, bookmark1.getId());

		// Then
		assertTrue(bookmarkOptional.isPresent());
		assertEquals(bookmark2.getId(), bookmarkOptional.get().getId());
	}
}
