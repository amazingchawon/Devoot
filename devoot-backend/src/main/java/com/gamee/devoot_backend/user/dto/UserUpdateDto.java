package com.gamee.devoot_backend.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.validator.ValidLinks;
import com.gamee.devoot_backend.user.validator.ValidTags;

public record UserUpdateDto(
	@NotBlank(message = "profileId cannot be blank")
	String profileId,
	@NotNull(message = "nickname cannot be null")
	@Size(max = 20, message = "nickname must not exceed 20 characters")
	String nickname,
	@ValidLinks
	String links,
	@NotNull(message = "isPublic cannot be null")
	Boolean isPublic,
	@ValidTags
	String tags
) {
	public void toEntity(User user) {
		user.setProfileId(profileId);
		user.setNickname(nickname);
		user.setLinks(links);
		user.setIsPublic(isPublic);
		user.setTags(tags);
	}
}
