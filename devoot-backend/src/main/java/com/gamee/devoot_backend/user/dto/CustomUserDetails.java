package com.gamee.devoot_backend.user.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gamee.devoot_backend.user.entity.User;

import lombok.Builder;

@Builder
public record CustomUserDetails(
	Long id,
	String email,
	String profileId,
	String nickname,
	String links,
	Boolean isPublic,
	String imageUrl,
	String tags
) implements UserDetails {

	public CustomUserDetails(User user) {
		this(
			user.getId(),
			user.getEmail(),
			user.getProfileId(),
			user.getNickname(),
			user.getLinks(),
			user.getIsPublic(),
			user.getImageUrl(),
			user.getTags()
		);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return profileId;
	}
}
