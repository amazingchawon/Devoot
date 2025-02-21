package com.gamee.devoot_backend.follow.service;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.exception.FollowCannotFollowSelfException;
import com.gamee.devoot_backend.follow.exception.FollowNotAuthorizedException;
import com.gamee.devoot_backend.follow.exception.FollowRelationshipAlreadyExists;
import com.gamee.devoot_backend.follow.exception.FollowRelationshipNotFound;
import com.gamee.devoot_backend.follow.exception.FollowRequestAlreadyAcceptedException;
import com.gamee.devoot_backend.follow.exception.FollowRequestPendingException;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.notification.entity.Notification;
import com.gamee.devoot_backend.notification.repository.NotificationRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.repository.UserRepository;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	@Transactional
	public Long createFollower(String followerProfileId, String followedProfileId) {
		User[] users = getUsersByProfileIds(followerProfileId, followedProfileId);
		User followerUser = users[0];
		User followedUser = users[1];
		Long followerId = followerUser.getId();
		Long followedId = followedUser.getId();

		// 이미 팔로우하고 있는지 확인
		followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
			.ifPresent(follow -> {
				throw new FollowRelationshipAlreadyExists();
			});

		// 팔로우 생성
		Boolean isAllowed = followedUser.getIsPublic();
		Follow follow = Follow.builder()
			.followerId(followerId)
			.followedId(followedId)
			.allowed(isAllowed)
			.build();
		Follow savedFollow = followRepository.save(follow);

		// 알림 생성
		Notification notification = Notification.builder()
			.toUserId(followedId)
			.fromUserId(followerId)
			.followId(savedFollow.getId())
			.hasRead(false)
			.build();
		notificationRepository.save(notification);

		return savedFollow.getId();
	}

	@Transactional
	public void deleteFollower(Long followId, Long currentUserId) {
		// 이미 팔로우 관계가 존재하는지 확인
		Follow follow = followRepository.findById(followId)
			.orElseThrow(FollowRelationshipNotFound::new);

		if (!follow.getFollowerId().equals(currentUserId)) {
			throw new FollowNotAuthorizedException();
		}
		followRepository.delete(follow);
		notificationRepository.deleteByFollowId(follow.getId());
	}

	public CustomPage<FollowUserDto> getFollowingUsers(String profileId, int page, int size) {
		User user = userService.findUserByProfileId(profileId);

		Page<Follow> followEntities = followRepository.findFollowingUsersByFollowerId(
			user.getId(), PageRequest.of(page - 1, size));

		Page<FollowUserDto> dtoPage = followEntities.map(follow -> FollowUserDto.fromEntity(follow, false));
		return new CustomPage<>(dtoPage);
	}

	public CustomPage<FollowUserDto> getFollowers(String profileId, int page, int size) {
		User user = userRepository.findByProfileId(profileId)
			.orElseThrow(UserNotFoundException::new);

		int adjustedPage = Math.max(page - 1, 0);
		Page<Follow> followEntities = followRepository.findFollowersByFollowedId(user.getId(), PageRequest.of(adjustedPage, size));

		Page<FollowUserDto> dtoPage = followEntities.map(follow -> FollowUserDto.fromEntity(follow, true));

		return new CustomPage<>(dtoPage);
	}

	private User[] getUsersByProfileIds(String followerProfileId, String followedProfileId) {
		if (followerProfileId.equals(followedProfileId)) {
			throw new FollowCannotFollowSelfException();
		}
		User followerUser = userRepository.findByProfileId(followerProfileId)
			.orElseThrow(UserNotFoundException::new);
		User followedUser = userRepository.findByProfileId(followedProfileId)
			.orElseThrow(UserNotFoundException::new);
		return new User[] {followerUser, followedUser};
	}

	public User validateAccessAndFetchFollowedUser(CustomUserDetails user, String profileId) {
		User followedUser = userRepository.findByProfileId(profileId)
			.orElseThrow(() -> new UserNotFoundException(String.format("User of %s not found", profileId)));

		// 자기 자신이 아니고, 상대방이 공개 계정이 아닐 경우에만 follow 요청 확인
		if (!user.id().equals(followedUser.getId()) && !followedUser.getIsPublic()) {
			followRepository.findIfAllowed(user.id(), followedUser.getId())
				.orElseThrow(FollowRequestPendingException::new);
		}

		return followedUser;
	}

	@Transactional
	public void acceptFollowRequest(Long followId, Long currentUserId) {
		Follow follow = followRepository.findById(followId)
			.orElseThrow(FollowRelationshipNotFound::new);
		if (!follow.getFollowedId().equals(currentUserId)) {
			throw new FollowNotAuthorizedException();
		}
		if (follow.getAllowed()) {
			throw new FollowRequestAlreadyAcceptedException();
		}
		follow.setAllowed(true);
		followRepository.save(follow);
	}
}
