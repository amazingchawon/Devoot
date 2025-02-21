package com.gamee.devoot_backend.user.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.gamee.devoot_backend.common.Util;
import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record UserDetailDto(
	Long id,
	String email,
	String profileId,
	String nickname,
	JsonNode links,
	Boolean isPublic,
	String imageUrl,
	String tags,
	Long followingCnt,
	Long followerCnt,
	Long bookmarkCnt,
	String followStatus,
	Long followId
) {
	public static UserDetailDto of(User user, Long followingCnt, Long followerCnt, Long bookmarkCnt,
		String followStatus, Long followId) {
		return UserDetailDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.profileId(user.getProfileId())
			.nickname(user.getNickname())
			.links(Util.parseToJson(user.getLinks()))
			.isPublic(user.getIsPublic())
			.imageUrl(user.getImageUrl())
			.tags(user.getTags())
			.followingCnt(followingCnt)
			.followerCnt(followerCnt)
			.bookmarkCnt(bookmarkCnt)
			.followStatus(followStatus)
			.followId(followId)
			.build();
	}
}
