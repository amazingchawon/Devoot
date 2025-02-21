package com.gamee.devoot_backend.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "toUserId", referencedColumnName = "id", insertable = false, updatable = false)
	private User toUser;
	@Column(name = "toUserId", nullable = false)
	private Long toUserId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FromUserId", referencedColumnName = "id", insertable = false, updatable = false)
	private User fromUser;
	@Column(name = "fromUserId", nullable = false)
	private Long fromUserId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followId", referencedColumnName = "id", insertable = false, updatable = false)
	private Follow follow;
	@Column(name = "followId", nullable = false)
	private Long followId;
	@Builder.Default
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	@Builder.Default
	@Column(nullable = false)
	private Boolean hasRead = false;
}
