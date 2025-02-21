package com.gamee.devoot_backend.follow;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.exception.FollowErrorCode;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.notification.repository.NotificationRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.repository.UserRepository;
import com.gamee.devoot_backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

	@Mock
	private FollowRepository followRepository;

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private FollowService followService;

	@Test
	@DisplayName("✅ 정상적인 팔로우 생성 요청 테스트")
	public void testCreateFollower_Success() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userB";
		Long generatedFollowId = 100L;

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();
		User followedUser = User.builder().id(2L).profileId(followedProfileId).build();

		Follow follow = Follow.builder()
			.id(generatedFollowId)
			.followerId(followerUser.getId())
			.followedId(followedUser.getId())
			.allowed(true)
			.build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.empty()); // 기존 팔로우 관계 없음
		when(followRepository.save(any(Follow.class))).thenReturn(follow);

		// When
		Long followId = followService.createFollower(followerProfileId, followedProfileId);

		// Then
		assertThat(followId).isEqualTo(generatedFollowId);
		verify(followRepository, times(1)).save(any(Follow.class));
		verify(notificationRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("❌ 존재하지 않는 사용자를 팔로우할 때 예외 발생")
	public void testCreateFollower_UserNotFound() {
		// Given
		String followerProfileId = "nonExistentUser";
		String followedProfileId = "userB";

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.createFollower(followerProfileId, followedProfileId))
			.isInstanceOf(DevootException.class);
	}

	@Test
	@DisplayName("❌ 이미 팔로우하고 있는 경우 예외 발생")
	public void testCreateFollower_AlreadyFollowing() {
		// Given
		String followerProfileId = "userA";
		String followedProfileId = "userB";

		User followerUser = User.builder().id(1L).profileId(followerProfileId).build();
		User followedUser = User.builder().id(2L).profileId(followedProfileId).build();

		Follow existingFollow = Follow.builder()
			.id(100L)
			.followerId(followerUser.getId())
			.followedId(followedUser.getId())
			.allowed(true)
			.build();

		when(userRepository.findByProfileId(followerProfileId)).thenReturn(Optional.of(followerUser));
		when(userRepository.findByProfileId(followedProfileId)).thenReturn(Optional.of(followedUser));
		when(followRepository.findByFollowerIdAndFollowedId(followerUser.getId(), followedUser.getId()))
			.thenReturn(Optional.of(existingFollow)); // 이미 팔로우한 상태

		// When & Then
		assertThatThrownBy(() -> followService.createFollower(followerProfileId, followedProfileId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_RELATIONSHIP_ALREADY_EXISTS);
	}

	@Test
	@DisplayName("정상적인 언팔로우 요청 테스트")
	public void testDeleteFollower_Success() {
		Long followId = 100L;
		Long currentUserId = 1L;

		Follow follow = Follow.builder()
			.id(followId)
			.followerId(currentUserId)
			.followedId(2L)
			.allowed(true)
			.build();

		when(followRepository.findById(followId)).thenReturn(Optional.of(follow));
		doNothing().when(followRepository).delete(follow);
		doNothing().when(notificationRepository).deleteByFollowId(followId);

		followService.deleteFollower(followId, currentUserId);

		verify(followRepository, times(1)).delete(follow);
		verify(notificationRepository, times(1)).deleteByFollowId(followId);
	}

	@Test
	@DisplayName("언팔로우 시 팔로우 관계가 존재하지 않을 경우 예외 발생")
	public void testDeleteFollower_RelationshipNotFound() {
		Long followId = 100L;
		Long currentUserId = 1L;

		when(followRepository.findById(followId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> followService.deleteFollower(followId, currentUserId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_RELATIONSHIP_NOT_FOUND);
	}

	@Test
	@DisplayName("언팔로우 시 본인이 아닌 경우 예외 발생")
	public void testDeleteFollower_NotAuthorized() {
		Long followId = 100L;
		Long currentUserId = 1L;

		Follow follow = Follow.builder()
			.id(followId)
			.followerId(2L) // 다른 사용자가 팔로우한 경우
			.followedId(3L)
			.allowed(true)
			.build();

		when(followRepository.findById(followId)).thenReturn(Optional.of(follow));

		assertThatThrownBy(() -> followService.deleteFollower(followId, currentUserId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_REQUEST_NOT_AUTHORIZED);
	}

	@Test
	@DisplayName("정상적인 팔로우 요청 수락 테스트")
	public void testAcceptFollowRequest_Success() {
		Long followId = 100L;
		Long currentUserId = 2L;

		Follow follow = Follow.builder()
			.id(followId)
			.followerId(1L)
			.followedId(currentUserId)
			.allowed(false) // 승인 대기 상태
			.build();

		when(followRepository.findById(followId)).thenReturn(Optional.of(follow));
		when(followRepository.save(any(Follow.class))).thenReturn(follow);

		followService.acceptFollowRequest(followId, currentUserId);

		verify(followRepository, times(1)).save(follow);
	}

	@Test
	@DisplayName("팔로우 요청이 이미 수락된 경우 예외 발생")
	public void testAcceptFollowRequest_AlreadyAccepted() {
		Long followId = 100L;
		Long currentUserId = 2L;

		Follow follow = Follow.builder()
			.id(followId)
			.followerId(1L)
			.followedId(currentUserId)
			.allowed(true) // 이미 승인된 상태
			.build();

		when(followRepository.findById(followId)).thenReturn(Optional.of(follow));

		assertThatThrownBy(() -> followService.acceptFollowRequest(followId, currentUserId))
			.isInstanceOf(DevootException.class)
			.hasFieldOrPropertyWithValue("errorCode", FollowErrorCode.FOLLOW_REQUEST_ALREADY_ACCEPTED);
	}

	// 팔로워 팔로잉 리스트

	/**
	 * ✅ A의 팔로잉 목록을 가져오는 테스트
	 * A가 팔로우한 사용자 목록을 가져올 때, 정상적으로 B가 팔로잉 목록에 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowingUsers() - A's following list")
	public void testGetFollowingUsers_A() {
		// Given
		String profileId = "userA";
		int page = 1, size = 20;
		int adjustedPage = page - 1; // 서비스에서 page - 1 적용

		User userA = User.builder().id(1L).profileId(profileId).build();
		User userB = User.builder().id(2L).profileId("userB").nickname("NicknameB").imageUrl("imageUrlB").build();

		Follow follow = Follow.builder()
			.followerId(userA.getId())
			.followedId(userB.getId())
			.followerUser(userA)
			.followedUser(userB)
			.allowed(true)
			.build();

		// Mock 설정
		when(userService.findUserByProfileId(profileId)).thenReturn(userA);
		when(followRepository.findFollowingUsersByFollowerId(eq(userA.getId()), eq(PageRequest.of(adjustedPage, size))))
			.thenReturn(new PageImpl<>(List.of(follow)));  // A는 B를 팔로우

		// When
		CustomPage<FollowUserDto> result = followService.getFollowingUsers(profileId, page, size);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().getFirst().profileId()).isEqualTo("userB");
		assertThat(result.getContent().getFirst().nickname()).isEqualTo("NicknameB");
		assertThat(result.getContent().getFirst().imageUrl()).isEqualTo("imageUrlB");

		// Print for debugging
		result.getContent().forEach(dto -> System.out.println("A is following: " + dto.profileId()));
	}

	/**
	 * ✅ B의 팔로워 목록을 가져오는 테스트
	 * B를 팔로우한 사용자 목록을 조회할 때, A와 C가 팔로워 목록에 포함되어 있는지 확인
	 */
	@Test
	@DisplayName("Test getFollowers() - B's follower list")
	public void testGetFollowers_B() {
		// Given
		String profileId = "userB";
		int page = 1, size = 20;
		int adjustedPage = page - 1;

		User userB = User.builder().id(2L).profileId(profileId).build();
		User userA = User.builder().id(1L).profileId("userA").nickname("NicknameA").imageUrl("imageUrlA").build();
		User userC = User.builder().id(3L).profileId("userC").nickname("NicknameC").imageUrl("imageUrlC").build();

		Follow followEntity1 = Follow.builder()
			.followerId(userA.getId())
			.followedId(userB.getId())
			.followerUser(userA)
			.followedUser(userB)
			.allowed(true)
			.build();

		Follow followEntity2 = Follow.builder()
			.followerId(userC.getId())
			.followedId(userB.getId())
			.followerUser(userC)
			.followedUser(userB)
			.allowed(true)
			.build();

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.of(userB));
		when(followRepository.findFollowersByFollowedId(eq(userB.getId()), eq(PageRequest.of(adjustedPage, size))))
			.thenReturn(new PageImpl<>(List.of(followEntity1, followEntity2)));  // B의 팔로워는 A와 C

		// When
		CustomPage<FollowUserDto> result = followService.getFollowers(profileId, page, size);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).profileId()).isEqualTo("userA");
		assertThat(result.getContent().get(0).nickname()).isEqualTo("NicknameA");
		assertThat(result.getContent().get(0).imageUrl()).isEqualTo("imageUrlA");

		assertThat(result.getContent().get(1).profileId()).isEqualTo("userC");
		assertThat(result.getContent().get(1).nickname()).isEqualTo("NicknameC");
		assertThat(result.getContent().get(1).imageUrl()).isEqualTo("imageUrlC");

		// Print for debugging
		result.getContent().forEach(dto -> System.out.println("B's followers: " + dto.profileId()));
	}

	/**
	 * ❌ 존재하지 않는 사용자를 팔로워 목록에서 조회할 때 UserNotFoundException 예외가 발생하는지 테스트
	 */
	@Test
	@DisplayName("Test getFollowers() - UserNotFoundException")
	public void testGetFollowers_UserNotFound() {
		// Given
		String profileId = "nonExistentUser";

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> followService.getFollowers(profileId, 1, 20))
			.isInstanceOf(UserNotFoundException.class);
	}

	@Test
	@DisplayName("Test getFollowers() - Empty followers list")
	public void testGetFollowers_EmptyList() {
		// Given
		String profileId = "userA"; // A는 팔로워가 없음
		int page = 1, size = 20;
		int adjustedPage = page - 1;

		User userA = User.builder().id(1L).profileId(profileId).build();

		// Mock 설정
		when(userRepository.findByProfileId(profileId)).thenReturn(Optional.of(userA));

		Page<Follow> emptyPage = new PageImpl<>(List.of(), PageRequest.of(adjustedPage, size), 0);

		doReturn(emptyPage).when(followRepository).findFollowersByFollowedId(eq(userA.getId()), any(PageRequest.class));

		// When
		CustomPage<FollowUserDto> result = followService.getFollowers(profileId, page, size);

		// 디버깅 출력 추가
		System.out.println("Followers count: " + result.getTotalElements());

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isEqualTo(0);
		assertThat(result.getTotalPages()).isEqualTo(0);
	}
}
