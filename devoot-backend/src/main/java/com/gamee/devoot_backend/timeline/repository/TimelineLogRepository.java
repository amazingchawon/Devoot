package com.gamee.devoot_backend.timeline.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gamee.devoot_backend.timeline.entity.TimelineLog;

@Repository
public interface TimelineLogRepository extends JpaRepository<TimelineLog, Long> {
	@Query("""
		SELECT a FROM TimelineLog a
		LEFT JOIN FETCH a.user
		LEFT JOIN FETCH TREAT(a as TodoLog).todo
		LEFT JOIN FETCH TREAT(a as BookmarkLog).lecture
		WHERE a.id = :id
		""")
	Optional<TimelineLog> findById(Long id);

	@Query("""
		SELECT a FROM TimelineLog a
		LEFT JOIN FETCH a.user
		LEFT JOIN FETCH TREAT(a as TodoLog).todo
		LEFT JOIN FETCH TREAT(a as BookmarkLog).lecture
		JOIN Follow f ON f.followedId = a.userId
		WHERE f.followerId = :userId
		AND f.allowed = true
		ORDER BY a.createdAt DESC
		""")
	Page<TimelineLog> getTimelineLogs(Long userId, Pageable pageable);
}
