package com.gamee.devoot_backend.todo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;

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
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TodoControllerIntegrationTest {
	User user = User.builder().id(1L).profileId("profileId").build();
	FirebaseService.DecodedToken mockToken = new FirebaseService.DecodedToken("mockUid", "devoot@gmail.com");
	FirebaseToken firebaseToken = mock(FirebaseToken.class);
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private FirebaseService firebaseService;

	@BeforeEach
	void setUp() throws Exception {
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
	@DisplayName("Test Exception when profile id mismatch")
	public void testException_whenProfileIdMismatch() throws Exception {
		// Given
		String diffProfileId = "diffProfileId";
		TodoCreateDto dto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture",
			"http://google.com", false);

		// When & Then
		mockMvc.perform(post("/api/users/{profileId}}/todos", diffProfileId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isForbidden())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value("USER_403_1"))
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
