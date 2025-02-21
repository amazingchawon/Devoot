package com.gamee.devoot_backend.notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	void deleteByFollowId(Long id);

	@Query("SELECT (COUNT(n) > 0) FROM Notification n WHERE n.toUserId = :userId AND n.hasRead = false")
	boolean existsByToUserIdAndHasReadFalse(@Param("userId") Long userId);

	@Modifying
	@Query("UPDATE Notification n SET n.hasRead = true WHERE n.toUserId = :toUserId")
	void markAllAsReadByUser(@Param("toUserId") Long toUserId);

	@Query("SELECT n FROM Notification n JOIN FETCH n.fromUser WHERE n.toUserId = :toUserId ORDER BY n.createdAt DESC")
	Page<Notification> findByToUserIdOrderByCreatedAtDesc(@Param("toUserId") Long toUserId, Pageable pageable);
}
