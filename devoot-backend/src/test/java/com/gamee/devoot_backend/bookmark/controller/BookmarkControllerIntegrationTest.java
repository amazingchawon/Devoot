package com.gamee.devoot_backend.bookmark.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.bookmark.dto.BookmarkCreateDto;
import com.gamee.devoot_backend.bookmark.dto.BookmarkUpdateDto;
import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookmarkControllerIntegrationTest {
	User user;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private EntityManager em;
	@MockitoBean
	private FirebaseService firebaseService;
	@Autowired
	private FollowService followService;
	@Autowired
	private BookmarkRepository bookmarkRepository;
	@MockitoBean
	private LectureRepository lectureRepository;

	@BeforeEach
	void setUp() throws Exception {
		FirebaseService.DecodedToken mockToken = new FirebaseService.DecodedToken("mockUid", "devoot@gmail.com");
		FirebaseToken firebaseToken = mock(FirebaseToken.class);
		user = User.builder().id(1L).profileId("profileId").build();

		when(firebaseService.parseToken(any())).thenReturn(mockToken);
		when(firebaseService.findUserByUid(any())).thenReturn(Optional.of(user));

		CustomUserDetails userDetails = CustomUserDetails.builder()
			.id(1L)
			.profileId("testProfileId")
			.build();

		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "password"));
	}

	@Test
	@DisplayName("Test addBookmark - throw Validation Error")
	public void testAddBookmark1() throws Exception {
		// Given
		BookmarkCreateDto createDto = new BookmarkCreateDto(null);

		// When & Then
		mockMvc.perform(post("/api/users/{profileId}/bookmarks", user.getProfileId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400_1"))
			.andDo(result -> {
				printResponse(result);
			});
	}

	@Test
	@DisplayName("Test addBookmark - successful")
	public void testAddBookmark2() throws Exception {
		// Given
		BookmarkCreateDto createDto = new BookmarkCreateDto(1L);
		when(lectureRepository.findById(1L))
			.thenReturn(Optional.of(Lecture.builder().id(1L).build()));

		// When & Then
		mockMvc.perform(post("/api/users/{profileId}/bookmarks", user.getProfileId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Test updateBookmark - throw validation error")
	public void testUpdateBookmark1() throws Exception {
		// Given
		BookmarkUpdateDto updateDto = new BookmarkUpdateDto(0, 2L);

		// When & Then
		mockMvc.perform(patch("/api/users/{profileId}/bookmarks/{bookmarkId}", user.getProfileId(), 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400_1"))
			.andDo(result -> {
				printResponse(result);
			});
	}

	@Test
	@DisplayName("Test updateBookmark")
	public void testUpdateBookmark2() throws Exception {
		// Given
		Bookmark bookmark = Bookmark.builder().lectureId(1L).userId(user.getId()).status(2).build();
		Bookmark beforeBookmark = Bookmark.builder()
			.lectureId(2L)
			.userId(user.getId())
			.status(bookmark.getStatus())
			.build();
		Bookmark newBeforeBookmark = Bookmark.builder()
			.lectureId(3L)
			.userId(user.getId())
			.status(3)
			.build();
		Bookmark bookmark2 = Bookmark.builder()
			.lectureId(4L)
			.userId(user.getId())
			.status(3)
			.build();

		bookmarkRepository.save(newBeforeBookmark);
		bookmarkRepository.save(beforeBookmark);
		bookmarkRepository.save(bookmark);
		bookmarkRepository.save(bookmark2);

		beforeBookmark.setNextId(bookmark.getId());
		newBeforeBookmark.setNextId(bookmark2.getId());
		bookmarkRepository.save(newBeforeBookmark);
		bookmarkRepository.save(beforeBookmark);

		BookmarkUpdateDto updateDto = new BookmarkUpdateDto(3, bookmark2.getId());

		em.flush();
		em.clear();

		// When & Then
		mockMvc.perform(patch("/api/users/{profileId}/bookmarks/{bookmarkId}", user.getProfileId(), bookmark.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isNoContent());
	}

	private void printResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object json = objectMapper.readValue(jsonResponse, Object.class); // Deserialize
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json); // Pretty print

		System.out.println(prettyJson);
	}
}
