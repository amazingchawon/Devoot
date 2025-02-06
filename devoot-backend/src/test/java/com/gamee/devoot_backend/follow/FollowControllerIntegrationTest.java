package com.gamee.devoot_backend.follow;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.DevootException;
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
@ActiveProfiles("test")
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

	// ======================= 팔로우(follow) 테스트 =======================

	/**
	 * ✅ 정상적인 팔로우 요청 (201 Created)
	 */
	@Test
	@DisplayName("정상적인 팔로우 요청")
	public void testCreateFollower_Success() throws Exception {
		String followerProfileId = "profileId";
		String followedProfileId = "targetUser";

		doNothing().when(followService).createFollower(followerProfileId, followedProfileId);

		mockMvc.perform(post("/api/users/{profileId}/follow", followedProfileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());

		verify(followService, times(1)).createFollower(followerProfileId, followedProfileId);
	}

	/**
	 * ❌ 자신을 팔로우하려는 경우 (400 Bad Request)
	 */
	@Test
	@DisplayName("자신을 팔로우하려는 경우 예외 발생")
	public void testCreateFollower_SelfFollow() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF))
			.when(followService).createFollower("profileId", "profileId");

		mockMvc.perform(post("/api/users/{profileId}/follow", "profileId")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getMessage()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("존재하지 않는 사용자 팔로우 시 예외 발생")
	public void testCreateFollower_UserNotFound() throws Exception {
		doThrow(new DevootException(UserErrorCode.USER_NOT_FOUND))
			.when(followService).createFollower("profileId", "nonexistentUser");

		mockMvc.perform(post("/api/users/{profileId}/follow", "nonexistentUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}

	/**
	 * ❌ 이미 팔로우한 경우 (409 Conflict)
	 */
	@Test
	@DisplayName("이미 팔로우한 경우 예외 발생")
	public void testCreateFollower_AlreadyFollowing() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS))
			.when(followService).createFollower("profileId", "targetUser");

		mockMvc.perform(post("/api/users/{profileId}/follow", "targetUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS.getMessage()));
	}

	/**
	 * ❌ 토큰 없이 팔로우 요청 (401 Unauthorized)
	 */
	@Test
	@DisplayName("토큰 없이 팔로우 요청 시 예외 발생")
	public void testCreateFollower_Unauthorized() throws Exception {
		mockMvc.perform(post("/api/users/{profileId}/follow", "targetUser")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	/**
	 * 📌 응답 JSON 출력
	 */
	private void printResponse(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
		String jsonResponse = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
		Object json = objectMapper.readValue(jsonResponse, Object.class); // Deserialize
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json); // Pretty print
		System.out.println(prettyJson);
	}

	// ======================= 언팔로우(unfollow) 테스트 =======================

	/**
	 * ✅ 정상적인 언팔로우 요청 (200 OK)
	 */
	@Test
	@DisplayName("정상적인 언팔로우 요청")
	public void testDeleteFollower_Success() throws Exception {
		String followerProfileId = "profileId";
		String followedProfileId = "targetUser";

		doNothing().when(followService).deleteFollower(followerProfileId, followedProfileId);

		mockMvc.perform(delete("/api/users/{profileId}/follow", followedProfileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(followService, times(1)).deleteFollower(followerProfileId, followedProfileId);
	}

	/**
	 * ❌ 자신을 언팔로우하려는 경우 (400 Bad Request)
	 */
	@Test
	@DisplayName("자신을 언팔로우하려는 경우 예외 발생")
	public void testDeleteFollower_SelfUnfollow() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF))
			.when(followService).deleteFollower("profileId", "profileId");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "profileId")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_CANNOT_FOLLOW_SELF.getMessage()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 언팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("존재하지 않는 사용자 언팔로우 시 예외 발생")
	public void testDeleteFollower_UserNotFound() throws Exception {
		doThrow(new DevootException(UserErrorCode.USER_NOT_FOUND))
			.when(followService).deleteFollower("profileId", "nonexistentUser");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "nonexistentUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}

	/**
	 * ❌ 이미 존재하지 않는 팔로우 관계를 언팔로우하려는 경우 (404 Not Found)
	 */
	@Test
	@DisplayName("이미 존재하지 않는 팔로우 관계 언팔로우 시 예외 발생")
	public void testDeleteFollower_RelationshipNotFound() throws Exception {
		doThrow(new DevootException(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND))
			.when(followService).deleteFollower("profileId", "targetUser");

		mockMvc.perform(delete("/api/users/{profileId}/follow", "targetUser")
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND.getMessage()));
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

		// Mock 설정
		when(followService.getFollowingUsers(profileId, 0, 20))
			.thenReturn(new PageImpl<>(List.of(followUserDTO)));  // A는 B를 팔로우

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/following", profileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].profileId").value("userB"))
			.andExpect(jsonPath("$.content[0].nickname").value("NicknameB"))
			.andExpect(jsonPath("$.content[0].imageUrl").value("imageUrlB"));

		verify(followService, times(1)).getFollowingUsers(profileId, 0, 20);
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
		when(followService.getFollowingUsers(profileId, 0, 20))
			.thenThrow(new DevootException(UserErrorCode.USER_NOT_FOUND));

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/following", profileId)
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

		// Mock 설정
		when(followService.getFollowers(profileId, 0, 20))
			.thenReturn(new PageImpl<>(List.of(followUserDTO1, followUserDTO2)));  // B의 팔로워는 A와 C

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/followers", profileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].profileId").value("userA"))
			.andExpect(jsonPath("$.content[1].profileId").value("userC"));

		verify(followService, times(1)).getFollowers(profileId, 0, 20);
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
		when(followService.getFollowers(profileId, 0, 20))
			.thenThrow(new DevootException(UserErrorCode.USER_NOT_FOUND));

		// When & Then
		mockMvc.perform(get("/api/users/{profileId}/followers", profileId)
				.header("Authorization", "Bearer mock-valid-token")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(UserErrorCode.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(UserErrorCode.USER_NOT_FOUND.getMessage()));
	}
}
