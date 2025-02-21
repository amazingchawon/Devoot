package com.gamee.devoot_backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.gamee.devoot_backend.user.validator.ValidLinks;
import com.gamee.devoot_backend.user.validator.ValidTags;

public record UserRegistrationDto(
	@NotBlank(message = "email cannot be blank")
	@Email(message = "Invalid email format")
	String email,
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
}
