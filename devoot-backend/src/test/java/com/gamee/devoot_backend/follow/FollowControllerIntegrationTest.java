package com.gamee.devoot_backend.follow;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowRequestDto;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.exception.FollowErrorCode;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.google.firebase.auth.FirebaseToken;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FollowControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private FirebaseService firebaseService;

	@MockitoBean
	private FollowService followService;

	private final User user = User.builder().id(1L).profileId("profileId").build();
	private final FirebaseService.DecodedToken mockToken = new FirebaseService.DecodedToken("mockUid", "devoot@gmail.com");
	private final FirebaseToken firebaseToken = mock(FirebaseToken.class);

	@BeforeEach
	void setUp() throws Exception {
		when(firebaseService.parseToken(any())).thenReturn(mockToken);
		when(firebaseService.findUserByUid(any())).thenReturn(Optional.of(user));

		CustomUserDetails userDetails = CustomUserDetails.builder()
			.id(1L)
			.profileId("profileId")
			.build();

		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "password"));
	}

	/** ✅ 정상적인 팔로우 요청 (201 Created) */
	@Test
	@DisplayName("정상적인 팔로우 요청")
	public void testCreateFollower_Success() throws Exception {
		// Given
		FollowRequestDto requestDto = new FollowRequestDto("targetUser");
		Long generatedFollowId = 100L;

		when(followService.createFollower("profileId", "targetUser")).thenReturn(generatedFollowId);

		// When & Then
		mockMvc.perform(post("/api/follows")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.followId").value(generatedFollowId.toString())); // 응답 값 검증
	}


	/** ✅ 정상적인 언팔로우 요청 (200 OK) */
	@Test
	@DisplayName("정상적인 언팔로우 요청")
	public void testDeleteFollower_Success() throws Exception {
		doNothing().when(followService).deleteFollower(1L, 1L);

		mockMvc.perform(delete("/api/follows/{followId}", 1L)
				.header("Authorization", "Bearer mock-valid-token"))
			.andExpect(status().isOk());
	}

	/** ✅ 정상적인 팔로우 요청 수락 (200 OK) */
	@Test
	@DisplayName("정상적인 팔로우 요청 수락")
	public void testAcceptFollowRequest_Success() throws Exception {
		doNothing().when(followService).acceptFollowRequest(1L, 1L);

		mockMvc.perform(post("/api/follows/{followId}/accept", 1L)
				.header("Authorization", "Bearer mock-valid-token"))
			.andExpect(status().isOk());
	}

	/** ❌ 이미 수락된 팔로우 요청 (409 Conflict) */
	@Test
	@DisplayName("이미 수락된 팔로우 요청 예외 발생")
	public void testAcceptFollowRequest_AlreadyAccepted() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_REQUEST_ALREADY_ACCEPTED))
			.when(followService).acceptFollowRequest(1L, 1L);

		mockMvc.perform(post("/api/follows/{followId}/accept", 1L)
				.header("Authorization", "Bearer mock-valid-token"))
			.andExpect(status().isConflict());
	}

	// ======================= 팔로우 리스트(following list) =======================

	/**
	 * ✅ 정상적인 팔로우 목록 조회 (200 OK)
	 * A가 팔로우한 사용자 목록을 조회할 때 B가 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowing() - A's following list")
	public void testGetFollowing_Success() throws Exception {
		// Given
		String profileId = "userA";
		FollowUserDto followUserDTO = new FollowUserDto("userB", "NicknameB", "imageUrlB"); // A는 B를 팔로우

		CustomPage<FollowUserDto> responsePage = new CustomPage<>(new PageImpl<>(List.of(followUserDTO)));

		// Mock 설정
		when(followService.getFollowingUsers(profileId, 1, 20))
			.thenReturn(responsePage);  // A는 B를 팔로우

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/following", profileId)
				.param("page", "1")
				.param("size", "20")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].profileId").value("userB"))
			.andExpect(jsonPath("$.content[0].nickname").value("NicknameB"))
			.andExpect(jsonPath("$.content[0].imageUrl").value("imageUrlB"));

		verify(followService, times(1)).getFollowingUsers(profileId, 1, 20);
	}

	/**
	 * ❌ 존재하지 않는 사용자의 팔로우 목록을 조회할 때 UserNotFoundException 예외가 발생하는지 테스트
	 */
	@Test
	@DisplayName("Test getFollowing() - UserNotFoundException")
	public void testGetFollowing_UserNotFound() throws Exception {
		// Given
		String profileId = "nonExistentUser";

		// Mock 설정
		when(followService.getFollowingUsers(profileId, 1, 20))
			.thenThrow(new DevootException(UserErrorCode.USER_NOT_FOUND));

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/following", profileId)
				.param("page", "1")
				.param("size", "20")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}

	// ======================= 팔로워 리스트(followers list) =======================

	/**
	 * ✅ 정상적인 팔로워 목록 조회 (200 OK)
	 * B의 팔로워 목록을 조회할 때 A와 C가 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowers() - B's follower list")
	public void testGetFollowers_Success() throws Exception {
		// Given
		String profileId = "userB";
		FollowUserDto followUserDTO1 = new FollowUserDto("userA", "NicknameA", "imageUrlA"); // B의 팔로워는 A
		FollowUserDto followUserDTO2 = new FollowUserDto("userC", "NicknameC", "imageUrlC"); // B의 팔로워는 C

		CustomPage<FollowUserDto> responsePage = new CustomPage<>(new PageImpl<>(List.of(followUserDTO1, followUserDTO2)));

		// Mock 설정
		when(followService.getFollowers(profileId, 1, 20))
			.thenReturn(responsePage);  // B의 팔로워는 A와 C

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/followers", profileId)
				.param("page", "1")
				.param("size", "20")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].profileId").value("userA"))
			.andExpect(jsonPath("$.content[1].profileId").value("userC"));

		verify(followService, times(1)).getFollowers(profileId, 1, 20);
	}

	/**
	 * ❌ 존재하지 않는 사용자의 팔로워 목록을 조회할 때 UserNotFoundException 예외가 발생하는지 테스트
	 */
	@Test
	@DisplayName("Test getFollowers() - UserNotFoundException")
	public void testGetFollowers_UserNotFound() throws Exception {
		// Given
		String profileId = "nonExistentUser";

		// Mock 설정
		when(followService.getFollowers(profileId, 1, 20))
			.thenThrow(new DevootException(UserErrorCode.USER_NOT_FOUND));

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/followers", profileId)
				.param("page", "1")
				.param("size", "20")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}
}
