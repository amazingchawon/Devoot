package com.gamee.devoot_backend.notification.service;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.notification.dto.NotificationDto;
import com.gamee.devoot_backend.notification.entity.Notification;
import com.gamee.devoot_backend.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public boolean hasUnreadNotifications(Long userId) {
		return notificationRepository.existsByToUserIdAndHasReadFalse(userId);
	}

	@Transactional
	public CustomPage<NotificationDto> getAndMarkNotificationsAsRead(Long userId, int page, int size) {
		notificationRepository.markAllAsReadByUser(userId);

		int adjustedPage = Math.max(page - 1, 0);
		Page<Notification> notifications = notificationRepository.findByToUserIdOrderByCreatedAtDesc(userId, PageRequest.of(adjustedPage, size));
		Page<NotificationDto> dtoPage = notifications.map(NotificationDto::fromEntity);
		return new CustomPage<>(dtoPage);
	}
}
