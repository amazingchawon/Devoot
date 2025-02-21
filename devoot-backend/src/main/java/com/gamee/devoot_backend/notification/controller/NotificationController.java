package com.gamee.devoot_backend.notification.controller;

import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.notification.dto.NotificationDto;
import com.gamee.devoot_backend.notification.service.NotificationService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Validated
public class NotificationController {
	private final NotificationService notificationService;

	@GetMapping("/unread")
	public ResponseEntity<Boolean> checkUnread(
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		boolean hasUnread = notificationService.hasUnreadNotifications(customUserDetails.id());
		return ResponseEntity.ok(hasUnread);
	}

	@GetMapping
	public ResponseEntity<CustomPage<NotificationDto>> getNotifications(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "20") @Positive int size
	) {
		CustomPage<NotificationDto> notificationPage = notificationService.getAndMarkNotificationsAsRead(customUserDetails.id(), page, size);
		return ResponseEntity.ok(notificationPage);
	}
}
