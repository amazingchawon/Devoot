package com.gamee.devoot_backend.user.dto;

import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record UserShortDetailDto(
	Long id,
	String profileId,
	String nickname,
	String imageUrl
) {
	public static UserShortDetailDto of(User user) {
		return UserShortDetailDto.builder()
			.id(user.getId())
			.profileId(user.getProfileId())
			.nickname(user.getNickname())
			.imageUrl(user.getImageUrl())
			.build();
	}
}
