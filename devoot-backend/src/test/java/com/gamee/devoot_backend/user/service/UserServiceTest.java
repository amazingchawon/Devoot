package com.gamee.devoot_backend.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserDetailDto;
import com.gamee.devoot_backend.user.dto.UserShortDetailDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	UserRepository userRepository;

	@Mock
	FollowRepository followRepository;

	@InjectMocks
	UserService userService;

	CustomUserDetails user = CustomUserDetails.builder()
		.id(1L)
		.links("")
		.profileId("testProfileId")
		.build();

	@Test
	@DisplayName("Test searchByPrefix()")
	void testSearchByPrefix() {
		// Given
		User user1 = User.builder().uid("1").profileId("devoot1").nickname("devoot").build();
		User user2 = User.builder().uid("2").profileId("devoot2").nickname("ssafy").build();
		User user3 = User.builder().uid("3").profileId("ssafy3").nickname("devoot").build();

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);

		when(userRepository.searchByPrefix("devoot", PageRequest.of(0, 10)))
			.thenReturn(new PageImpl<>(List.of(user1, user2, user3)));

		// When
		CustomPage<UserShortDetailDto> userSearchDetailDtos = userService.searchByPrefix("devoot", 1, 10);

		// Then
		assertEquals(userSearchDetailDtos.getTotalElements(), 3);
	}

	@Test
	@DisplayName("Test getUserInfo - successful(if viewing oneself)")
	void testGetUserInfo1() {
		// Given
		User sameUser = User.builder().id(user.id()).profileId(user.profileId()).build();
		Map<String, Long> userStats = new HashMap<>(Map.of(
			"followingCnt", 200L,
			"followerCnt", 150L,
			"bookmarkCnt", 50L
		));

		when(userRepository.findByProfileId(sameUser.getProfileId()))
			.thenReturn(Optional.of(sameUser));
		when(userRepository.getUserStatsAsMap(sameUser.getId()))
			.thenReturn(userStats);

		// When
		UserDetailDto dto = userService.getUserInfo(user, user.profileId());

		// Then
		verifyNoInteractions(followRepository);
		verify(userRepository, times(1)).getUserStatsAsMap(sameUser.getId());
		verify(userRepository, times(1)).findByProfileId(sameUser.getProfileId());
		assertNull(dto.followStatus());
		assertEquals(sameUser.getProfileId(), dto.profileId());
		assertEquals(200L, dto.followingCnt());
		assertEquals(150L, dto.followerCnt());
		assertEquals(50L, dto.bookmarkCnt());
	}

	@Test
	@DisplayName("Test getUserInfo - successful(NOTFOLLOWING and viewing different user)")
	void testGetUserInfo2() {
		// Given
		String diffProfileId = "diffProfileId";
		User diffUser = User.builder().id(2L).uid("diffUserUid").profileId(diffProfileId).build();
		Map<String, Long> userStats = new HashMap<>(Map.of(
			"followingCnt", 200L,
			"followerCnt", 150L,
			"bookmarkCnt", 50L
		));

		when(userRepository.findByProfileId(diffProfileId))
			.thenReturn(Optional.of(diffUser));
		when(userRepository.getUserStatsAsMap(diffUser.getId()))
			.thenReturn(userStats);

		// When
		UserDetailDto dto = userService.getUserInfo(user, diffProfileId);

		// Then
		verify(followRepository, times(1)).findByFollowerIdAndFollowedId(any(), any());
		verify(userRepository, times(1)).getUserStatsAsMap(diffUser.getId());
		verify(userRepository, times(1)).findByProfileId(diffUser.getProfileId());
		assertEquals("NOTFOLLOWING", dto.followStatus());
		assertEquals(diffProfileId, dto.profileId());
		assertEquals(200L, dto.followingCnt());
		assertEquals(150L, dto.followerCnt());
		assertEquals(50L, dto.bookmarkCnt());
	}

	@Test
	@DisplayName("Test getUserInfo - successful(PENDING and viewing different user)")
	void testGetUserInfo3() {
		// Given
		String diffProfileId = "diffProfileId";
		User diffUser = User.builder().id(2L).uid("diffUserUid").profileId(diffProfileId).build();
		Map<String, Long> userStats = new HashMap<>(Map.of(
			"followingCnt", 200L,
			"followerCnt", 150L,
			"bookmarkCnt", 50L
		));
		Follow follow = Follow.builder()
			.followerId(user.id())
			.followedId(diffUser.getId())
			.build();

		when(userRepository.findByProfileId(diffProfileId))
			.thenReturn(Optional.of(diffUser));
		when(userRepository.getUserStatsAsMap(diffUser.getId()))
			.thenReturn(userStats);
		when(followRepository.findByFollowerIdAndFollowedId(user.id(), diffUser.getId()))
			.thenReturn(Optional.of(follow));
		// When
		UserDetailDto dto = userService.getUserInfo(user, diffProfileId);

		// Then
		verify(followRepository, times(1)).findByFollowerIdAndFollowedId(any(), any());
		verify(userRepository, times(1)).getUserStatsAsMap(diffUser.getId());
		verify(userRepository, times(1)).findByProfileId(diffUser.getProfileId());
		assertEquals("PENDING", dto.followStatus());
		assertEquals(diffProfileId, dto.profileId());
		assertEquals(200L, dto.followingCnt());
		assertEquals(150L, dto.followerCnt());
		assertEquals(50L, dto.bookmarkCnt());
	}

	@Test
	@DisplayName("Test getUserInfo - successful(FOLLOWING and viewing different user)")
	void testGetUserInfo4() throws JsonProcessingException {
		// Given
		String diffProfileId = "diffProfileId";
		User diffUser = User.builder().id(2L).uid("diffUserUid").profileId(diffProfileId).build();
		Map<String, Long> userStats = new HashMap<>(Map.of(
			"followingCnt", 200L,
			"followerCnt", 150L,
			"bookmarkCnt", 50L
		));

		Follow follow = Follow.builder()
			.followerId(user.id())
			.followedId(diffUser.getId())
			.allowed(true)
			.build();

		when(userRepository.findByProfileId(diffProfileId))
			.thenReturn(Optional.of(diffUser));
		when(userRepository.getUserStatsAsMap(diffUser.getId()))
			.thenReturn(userStats);
		when(followRepository.findByFollowerIdAndFollowedId(user.id(), diffUser.getId()))
			.thenReturn(Optional.of(follow));

		// When
		UserDetailDto dto = userService.getUserInfo(user, diffProfileId);

		// Then
		verify(followRepository, times(1)).findByFollowerIdAndFollowedId(any(), any());
		verify(userRepository, times(1)).getUserStatsAsMap(diffUser.getId());
		verify(userRepository, times(1)).findByProfileId(diffUser.getProfileId());
		assertEquals("FOLLOWING", dto.followStatus());
		assertEquals(diffProfileId, dto.profileId());
		assertEquals(200L, dto.followingCnt());
		assertEquals(150L, dto.followerCnt());
		assertEquals(50L, dto.bookmarkCnt());
	}
}
