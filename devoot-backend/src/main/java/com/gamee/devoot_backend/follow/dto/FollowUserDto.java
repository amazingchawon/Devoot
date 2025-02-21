package com.gamee.devoot_backend.follow.dto;

import com.gamee.devoot_backend.follow.entity.Follow;

public record FollowUserDto(
	String profileId,
	String nickname,
	String imageUrl
) {
	public static FollowUserDto fromEntity(Follow follow, boolean isFollower) {
		if (isFollower) {
			return new FollowUserDto(
				follow.getFollowerUser().getProfileId(),
				follow.getFollowerUser().getNickname(),
				follow.getFollowerUser().getImageUrl()
			);
		} else {
			return new FollowUserDto(
				follow.getFollowedUser().getProfileId(),
				follow.getFollowedUser().getNickname(),
				follow.getFollowedUser().getImageUrl()
			);
		}
	}
}
