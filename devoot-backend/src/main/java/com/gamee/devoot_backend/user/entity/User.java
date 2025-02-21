package com.gamee.devoot_backend.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
	name = "\"User\"",
	indexes = {
		@Index(name = "idx_profileid", columnList = "profileId"),
		@Index(name = "idx_nickname", columnList = "nickname")
	}
)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String uid;

	private String email;

	@Column(unique = true)
	private String profileId;

	private String nickname;

	@Column(columnDefinition = "JSON")
	private String links;

	@Builder.Default
	private Boolean isPublic = true;

	private String imageUrl;

	private String tags;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}
