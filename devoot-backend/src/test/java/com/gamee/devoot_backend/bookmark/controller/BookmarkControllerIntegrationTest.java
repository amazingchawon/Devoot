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
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
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

		// When & Then
		mockMvc.perform(post("/api/users/{profileId}/bookmarks", user.getProfileId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("Test getBookmarks - throws Permission denied exception")
	public void testGetBookmarks1() throws Exception {
		// Given
		String diffProfileId = "diffProfileId";

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/bookmarks", diffProfileId)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("USER_403_1"))
			.andDo(result -> {
				printResponse(result);
			});
	}

	@Test
	@DisplayName("Test updateBookmark - successful")
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

	private void printResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);

		Object json = objectMapper.readValue(jsonResponse, Object.class); // Deserialize
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json); // Pretty print

		System.out.println(prettyJson);
	}
}
