package com.gamee.devoot_backend.notification.dto;

import com.gamee.devoot_backend.notification.entity.Notification;

public record NotificationDto(
	Long id,
	String fromUserProfileId,
	String fromUserNickname,
	String fromUserImageUrl,
	Long followId,
	Boolean hasRead,
	Boolean pending  // 팔로우 요청이 아직 수락되지 않은 경우 (allowed가 false)
) {
	public static NotificationDto fromEntity(Notification notification) {
		boolean pending = (notification.getFollow() != null && !notification.getFollow().getAllowed());
		return new NotificationDto(
			notification.getId(),
			notification.getFromUser().getProfileId(),
			notification.getFromUser().getNickname(),
			notification.getFromUser().getImageUrl(),
			notification.getFollowId(),
			notification.getHasRead(),
			pending
		);
	}
}
