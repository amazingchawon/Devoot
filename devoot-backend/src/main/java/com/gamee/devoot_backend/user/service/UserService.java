package com.gamee.devoot_backend.user.service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserDetailDto;
import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.dto.UserShortDetailDto;
import com.gamee.devoot_backend.user.dto.UserUpdateDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserAlreadyExistsException;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.exception.UserProfileIdAlreadyExistsException;
import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;
import com.gamee.devoot_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final S3Service s3Service;

	public boolean existsUserByUid(String uid) {
		return userRepository.existsByUid(uid);
	}

	public boolean existsByProfileId(String profileId, CustomUserDetails userDetails) {
		if (userDetails != null && (userDetails.profileId().equals(profileId))) {
			return false;
		}
		return userRepository.existsByProfileId(profileId);
	}

	@Transactional
	public User registerUser(String uid, UserRegistrationDto userRegistrationDto, MultipartFile file) {
		if (existsUserByUid(uid)) {
			throw new UserAlreadyExistsException();
		}

		if (userRegistrationDto.profileId() != null
			&& existsByProfileId(userRegistrationDto.profileId(), null)) {
			throw new UserProfileIdAlreadyExistsException();
		}

		User newUser = User.builder()
			.uid(uid)
			.email(userRegistrationDto.email())
			.profileId(userRegistrationDto.profileId())
			.nickname(userRegistrationDto.nickname())
			.links(userRegistrationDto.links())
			.isPublic(userRegistrationDto.isPublic())
			.imageUrl(null)
			.tags(userRegistrationDto.tags())
			.build();

		userRepository.save(newUser);
		if (file != null && !file.isEmpty()) {
			String imageUrl = s3Service.uploadFile(newUser.getId(), file);
			newUser.setImageUrl(imageUrl);
			userRepository.save(newUser);
		}

		return newUser;
	}

	public UserDetailDto getUserInfo(CustomUserDetails userDetails, String profileId) {
		User user = userRepository.findByProfileId(profileId)
			.orElseThrow(() -> new UserNotFoundException(profileId));

		Map<String, Long> userStats = userRepository.getUserStatsAsMap(user.getId());

		AtomicReference<String> followStatus = new AtomicReference<>();
		AtomicReference<Long> followId = new AtomicReference<>();
		if (!user.getId().equals(userDetails.id())) {
			followRepository.findByFollowerIdAndFollowedId(userDetails.id(), user.getId())
				.ifPresentOrElse(
					follow -> {
						followId.set(follow.getId());
						if (follow.getAllowed()) {
							followStatus.set("FOLLOWING");
						} else {
							followStatus.set("PENDING");
						}
					},
					() -> {
						followStatus.set("NOTFOLLOWING");
					}
				);
		}

		return UserDetailDto.of(
			user,
			userStats.get("followingCnt"),
			userStats.get("followerCnt"),
			userStats.get("bookmarkCnt"),
			followStatus.get(),
			followId.get()
		);

	}

	@Transactional
	public User updateUser(Long userId, UserUpdateDto userUpdateDto, MultipartFile file) {
		User user = userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);

		if (!user.getProfileId().equals(userUpdateDto.profileId())
			&& userRepository.existsByProfileId(userUpdateDto.profileId())) {
			throw new UserProfileIdAlreadyExistsException();
		}

		user.setProfileId(userUpdateDto.profileId());
		user.setNickname(userUpdateDto.nickname());
		user.setLinks(userUpdateDto.links());
		user.setIsPublic(userUpdateDto.isPublic());
		user.setTags(userUpdateDto.tags());

		if (file != null && !file.isEmpty()) {
			String imageUrl = s3Service.uploadFile(userId, file);
			user.setImageUrl(imageUrl);
		}
		return userRepository.save(user);
	}

	public void checkUserMatchesProfileId(CustomUserDetails user, String profileId) {
		if (!user.profileId().equals(profileId)) {
			throw new UserProfileIdMismatchException();
		}
	}

	public CustomPage<UserShortDetailDto> searchByPrefix(String query, int page, int size) {
		return new CustomPage<>(
			userRepository.searchByPrefix(query, PageRequest.of(page - 1, size))
				.map(UserShortDetailDto::of)
		);
	}
}
