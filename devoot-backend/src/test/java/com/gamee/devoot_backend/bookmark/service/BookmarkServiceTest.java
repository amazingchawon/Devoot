package com.gamee.devoot_backend.bookmark.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.bookmark.dto.BookmarkCreateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkUpdateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkWithLectureDetailDto;
import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.exception.DuplicateBookmarkException;
import com.gamee.devoot_backend.bookmark.repository.BookmarkLogRepository;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {
	@Mock
	BookmarkRepository bookmarkRepository;

	@Mock
	BookmarkLogRepository bookmarkLogRepository;

	@Mock
	LectureRepository lectureRepository;

	@Mock
	UserService userService;

	@Mock
	FollowService followService;

	@InjectMocks
	BookmarkService bookmarkService;

	CustomUserDetails user = CustomUserDetails.builder()
		.id(1L)
		.profileId("testProfileId")
		.build();

	User followedUser = User.builder().id(2L).profileId("followedUserProfileId").build();

	BookmarkCreateDto createDto = new BookmarkCreateDto(1L);
	BookmarkUpdateDto updateDto = new BookmarkUpdateDto(3, 2L);

	@Test
	@DisplayName("Test addBookmark() - no existing bookmark")
	public void testAddBookmark1() {
		// Given
		when(bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), 1, 0L))
			.thenReturn(Optional.empty());
		when(lectureRepository.findById(createDto.lectureId()))
			.thenReturn(Optional.of(Lecture.builder().id(createDto.lectureId()).build()));
		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());

		// When
		bookmarkService.addBookmark(user, user.profileId(), createDto);

		// Then
		verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
		verify(bookmarkLogRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("Test addBookmark() - when bookmark exists")
	public void testAddBookmark2() {
		// Given
		Bookmark existingBookmark = Bookmark.builder()
			.lectureId(createDto.lectureId() + 1)
			.userId(user.id())
			.status(1)
			.nextId(0L)
			.build();
		when(bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), 1, 0L))
			.thenReturn(Optional.of(existingBookmark));
		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(lectureRepository.findById(createDto.lectureId()))
			.thenReturn(Optional.of(Lecture.builder().id(createDto.lectureId()).build()));

		// When
		bookmarkService.addBookmark(user, user.profileId(), createDto);

		// Then
		verify(bookmarkRepository, times(2)).save(any(Bookmark.class));
		verify(bookmarkLogRepository, times(1)).save(any());
		assertNotNull(existingBookmark.getNextId());
	}

	@Test
	@DisplayName("Test addBookmark() - throws Duplicate bookmark exception")
	public void testAddBookmark3() {
		// Given
		Long bookmarkLectureId = 1L;
		Bookmark bookmark = Bookmark.builder().build();
		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(bookmarkRepository.findByUserIdAndLectureId(user.id(), bookmarkLectureId))
			.thenReturn(Optional.of(bookmark));
		when(lectureRepository.findById(createDto.lectureId()))
			.thenReturn(Optional.of(Lecture.builder().id(createDto.lectureId()).build()));

		// When & Then
		assertThatThrownBy(() -> bookmarkService.addBookmark(user, user.profileId(), createDto))
			.isInstanceOf(DuplicateBookmarkException.class);
		verifyNoInteractions(bookmarkLogRepository);
		verifyNoMoreInteractions(bookmarkRepository);
	}

	@Test
	@DisplayName("Test getBookmarks() - when existing")
	public void testGetBookmarks1() throws JsonProcessingException {
		// Given
		List<Lecture> lectures = Arrays.asList(
			Lecture.builder()
				.id(1L)
				.name("Spring Boot for Beginners")
				.sourceName("Inflearn")
				.tags("spring,java,web")
				.imageUrl("https://cdn.inflearn.com/spring-boot-basics.jpg")
				.curriculum(
					"{\"1\": {\"majorTitle\": \"INTRO - 강의소개\", \"subLectures\": [{\"title\": \"AR 증강현실 입문 강의 소개\", \"time\": \"\"}]}, \"2\": {\"majorTitle\": \"환경 세팅 및 데모앱 살펴보기\", \"subLectures\": [{\"title\": \"수강 전 사전 준비사항\", \"time\": \"\"}, {\"title\": \"유니티 설치 및 환경 설정\", \"time\": \"\"}, {\"title\": \"증강현실 데모 앱 실행을 통해 증강현실 쉽게 이해하기\", \"time\": \"\"}]}, \"3\": {\"majorTitle\": \"동물 이미지 인식을 통한 증강\", \"subLectures\": [{\"title\": \"늑대 이미지를 3D로 재탄생 시켜보기\", \"time\": \"\"}, {\"title\": \"증강 된 늑대의 애니메이션 변경 해보기\", \"time\": \"\"}, {\"title\": \"애니메이션에 효과음 추가해보기\", \"time\": \"\"}, {\"title\": \"여러 동물이미지를 3D로 재탄생 시켜보기\", \"time\": \"\"}]}, \"4\": {\"majorTitle\": \"평면 인식을 통한 증강\", \"subLectures\": [{\"title\": \"바닥 위에 고양이 증강시켜보기\", \"time\": \"\"}, {\"title\": \"터치를 통해 고양이 이동시켜보기\", \"time\": \"\"}, {\"title\": \"코드를 통해 애니메이션과 효과음 추가해보기\", \"time\": \"\"}, {\"title\": \"고양이에게 이름 지어주기\", \"time\": \"\"}, {\"title\": \"앱 공유방법 소개\", \"time\": \"\"}]}, \"5\": {\"majorTitle\": \"OUTRO - 강의를 마치며\", \"subLectures\": [{\"title\": \"증강현실 프로젝트 회고 및 시리즈 강의 소개\", \"time\": \"\"}]}}")
				.build(),
			Lecture.builder()
				.id(2L)
				.name("Advanced React Patterns")
				.sourceName("Udemy")
				.tags("react,javascript,frontend")
				.imageUrl("https://cdn.udemy.com/react-advanced.jpg")
				.curriculum("{\"1\": {\"majorTitle\": \"Kotlin으로 만드는 심플셀스타그램 앱\", \"subLectures\": [{\"title\": \"Hello Android Studio\", \"time\": \"\"}]}}")
				.build(),
			Lecture.builder()
				.id(3L)
				.name("Machine Learning Fundamentals")
				.sourceName("Coursera")
				.tags("python,ml,ai")
				.imageUrl("https://cdn.coursera.org/ml-fundamentals.jpg")
				.curriculum("{\"1\": {\"majorTitle\": \"컴퓨팅 사고력이란?\", \"subLectures\": [{\"title\": \"문제를 해결하는 새로운 시각이 필요해\", \"time\": \"\"}]}}")
				.build(),
			Lecture.builder()
				.id(4L)
				.name("DevOps with Docker & Kubernetes")
				.sourceName("PluralSight")
				.tags("docker,kubernetes,devops")
				.imageUrl("https://cdn.pluralsight.com/devops-k8s.jpg")
				.curriculum("{\"1\": {\"majorTitle\": \"자연어 처리\", \"subLectures\": [{\"title\": \"자연어 처리와 ChatGPT의 원리\", \"time\": \"\"}]}}")
				.build(),
			Lecture.builder()
				.id(5L)
				.name("Microservices Architecture")
				.sourceName("LinkedIn Learning")
				.tags("microservices,architecture,cloud")
				.imageUrl("https://cdn.linkedin.com/microservices.jpg")
				.curriculum("{\"1\": {\"majorTitle\": \"1. 파이썬 개요 및 설치\", \"subLectures\": [{\"title\": \"파이썬 데이터분석 과정 학습 범위 설명\", \"time\": \"\"}]}}")
				.build()
		);
		List<Bookmark> bookmarks = Arrays.asList(
			// todo: 2 -> 5, doing: 1 -> 3, done: 4
			Bookmark.builder()
				.id(1L)
				.lecture(lectures.get(0))
				.userId(followedUser.getId())
				.status(2)
				.nextId(3L)
				.build(),
			Bookmark.builder()
				.id(2L)
				.lecture(lectures.get(1))
				.userId(followedUser.getId())
				.status(1)
				.nextId(5L)
				.build(),
			Bookmark.builder()
				.id(3L)
				.lecture(lectures.get(2))
				.userId(followedUser.getId())
				.status(2)
				.nextId(0L)
				.build(),
			Bookmark.builder()
				.id(4L)
				.lecture(lectures.get(3))
				.userId(followedUser.getId())
				.status(3)
				.nextId(0L)
				.build(),
			Bookmark.builder()
				.id(5L)
				.lecture(lectures.get(4))
				.userId(followedUser.getId())
				.status(1)
				.nextId(0L)
				.build()
		);

		when(followService.validateAccessAndFetchFollowedUser(user, followedUser.getProfileId())).thenReturn(
			followedUser);
		when(bookmarkRepository.findBookmarksByUserId(followedUser.getId()))
			.thenReturn(bookmarks);
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 1))
			.thenReturn(Optional.of(bookmarks.get(1)));
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 2))
			.thenReturn(Optional.of(bookmarks.get(0)));
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 3))
			.thenReturn(Optional.of(bookmarks.get(3)));

		// When
		Map<String, List<BookmarkWithLectureDetailDto>> res = bookmarkService.getBookmarks(user, followedUser.getProfileId());

		// Then
		assertEquals(res.size(), 3);
		assertEquals(res.get("todo").size(), 2);
		assertEquals(res.get("in-progress").size(), 2);
		assertEquals(res.get("done").size(), 1);
		assertEquals(res.get("todo").get(0).id(), bookmarks.get(1).getId());
		assertEquals(res.get("todo").get(1).id(), bookmarks.get(4).getId());
		assertEquals(res.get("in-progress").get(0).id(), bookmarks.get(0).getId());
		assertEquals(res.get("in-progress").get(1).id(), bookmarks.get(2).getId());
		assertEquals(res.get("done").get(0).id(), bookmarks.get(3).getId());

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res);
		System.out.println(json);
	}

	@Test
	@DisplayName("Test getBookmarks() - when no bookmarks")
	public void testGetBookmarks2() throws JsonProcessingException {
		// Given
		when(followService.validateAccessAndFetchFollowedUser(user, followedUser.getProfileId())).thenReturn(
			followedUser);
		when(bookmarkRepository.findBookmarksByUserId(followedUser.getId()))
			.thenReturn(new ArrayList<>());
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 1))
			.thenReturn(Optional.empty());
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 2))
			.thenReturn(Optional.empty());
		when(bookmarkRepository.findFirstBookmarkOf(followedUser.getId(), 3))
			.thenReturn(Optional.empty());

		// When
		Map<String, List<BookmarkWithLectureDetailDto>> res = bookmarkService.getBookmarks(user, followedUser.getProfileId());

		// Then
		assertEquals(res.size(), 3);
		assertEquals(res.get("todo").size(), 0);
		assertEquals(res.get("in-progress").size(), 0);
		assertEquals(res.get("done").size(), 0);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res);
		System.out.println(json);
	}

	@Test
	@DisplayName("Test updateBookmark - when status and order change")
	public void testUpdateBookmark1() {
		// Given
		Bookmark bookmark = Bookmark.builder().id(1L).lectureId(1L).userId(user.id()).status(2).nextId(3L).build();

		Integer beforeStatus = bookmark.getStatus();
		Long beforeNextId = bookmark.getNextId();

		Integer newStatus = updateDto.status();
		Long newNextId = updateDto.nextId();

		Bookmark beforeBookmark = Bookmark.builder()
			.id(2L)
			.lectureId(2L)
			.userId(user.id())
			.status(bookmark.getStatus())
			.nextId(bookmark.getId())
			.build();
		Bookmark newBeforeBookmark = Bookmark.builder()
			.id(3L)
			.lectureId(3L)
			.userId(user.id())
			.status(newStatus)
			.nextId(newNextId)
			.build();

		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(bookmarkRepository.findById(bookmark.getId()))
			.thenReturn(Optional.of(bookmark));
		when(bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), beforeStatus, bookmark.getId()))
			.thenReturn(Optional.of(beforeBookmark));
		when(bookmarkRepository.findByUserIdAndStatusAndNextId(user.id(), newStatus, newNextId))
			.thenReturn(Optional.of(newBeforeBookmark));

		// When
		bookmarkService.updateBookmark(user, user.profileId(), bookmark.getId(), updateDto);

		// Then
		verify(bookmarkRepository, times(3)).save(any());
		verify(bookmarkLogRepository, times(1)).save(any());
		assertEquals(beforeBookmark.getNextId(), beforeNextId);
		assertEquals(newBeforeBookmark.getNextId(), bookmark.getId());
	}

	@Test
	@DisplayName("Test deleteBookmark")
	public void testDeleteBookmark1() {
		// Given
		Bookmark bookmark = Bookmark.builder().id(1L).lectureId(1L).userId(user.id()).status(2).nextId(3L).build();

		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(bookmarkRepository.findById(bookmark.getId()))
			.thenReturn(Optional.of(bookmark));

		// When
		bookmarkService.deleteBookmark(user, user.profileId(), bookmark.getId());

		// Then
		verify(bookmarkRepository, times(1)).delete(any());
	}
}
