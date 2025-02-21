package com.gamee.devoot_backend.user.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gamee.devoot_backend.user.entity.Admin;
import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record AdminDetailDto(
	Long userId,
	String profileId,
	String email,
	@JsonFormat(pattern = "yyyy-MM-dd")
	LocalDate createdAt
) {
	public static AdminDetailDto of(Admin admin) {
		User user = admin.getUser();
		return AdminDetailDto.builder()
			.userId(user.getId())
			.profileId(user.getProfileId())
			.email(user.getEmail())
			.createdAt(LocalDate.from(admin.getCreatedAt()))
			.build();
	}
}
