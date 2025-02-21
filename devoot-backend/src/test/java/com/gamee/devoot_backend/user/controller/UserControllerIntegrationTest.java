package com.gamee.devoot_backend.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserShortDetailDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.gamee.devoot_backend.user.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
	@MockitoBean
	FirebaseService firebaseService;
	@MockitoBean
	UserService userService;
	User user;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {
		FirebaseService.DecodedToken mockToken = new FirebaseService.DecodedToken("mockUid", "devoot@gmail.com");
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
	@DisplayName("Test searchUsers() - successful")
	public void testSearchUsers1() throws Exception {
		// Given
		String query = "devoot user";
		Page<User> userPage = new PageImpl<>(List.of(
			User.builder()
				.id(1L)
				.uid("1")
				.profileId("devoot user 1")
				.nickname("devoot user")
				.imageUrl(
					"https://cphinf.pstatic.net/mooc/20241216_40/17343141727019T4cw_PNG/%BA%CE%C4%DA_%B0%AD%C0%C7%C4%AB%B5%E5_github_241213.png")
				.build(),
			User.builder()
				.id(2L)
				.uid("2")
				.profileId("devoot user2")
				.nickname("ssafy")
				.imageUrl(
					"https://cphinf.pstatic.net/mooc/20201217_255/1608193207620NDOSQ_PNG/boostcourse_html_css_720_426.png")
				.build(),
			User.builder()
				.id(3L)
				.uid("3")
				.profileId("ssafy3")
				.nickname("devoot user")
				.imageUrl("https://cphinf.pstatic.net/mooc/20201217_218/1608193471491p0oHx_PNG/img_boost_web2.png")
				.build()
		));
		when(userService.searchByPrefix(query, 1, 10))
			.thenReturn(new CustomPage<>(userPage.map(UserShortDetailDto::of)));

		// When & Then
		mockMvc.perform(get("/api/users")
				.param("q", query)
				.param("page", "1")
				.param("size", "10")
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isOk())
			.andDo(print());

		verify(userService).searchByPrefix(query, 1, 10);
	}

	@Test
	@DisplayName("Test searchUsers() - throw excpetion when query not provided")
	public void testSearchUsers2() throws Exception {
		// When & Then
		mockMvc.perform(get("/api/users")
				.header("Authorization", "Bearer yourValidToken")
			)
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@DisplayName("Test searchUsers() - when page or size is invalid")
	public void testSearchUsers3() throws Exception {
		// Given
		String query = "query";
		// When & Then
		mockMvc.perform(get("/api/users")
				.header("Authorization", "Bearer yourValidToken")
				.param("q", query)
				.param("page", "0")
				.param("size", "-1")
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400_1"))
			.andDo(print());
	}
}
