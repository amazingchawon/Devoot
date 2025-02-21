package com.gamee.devoot_backend.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;

public interface BookmarkLogRepository extends JpaRepository<BookmarkLog, Long> {
	@Query("""
		SELECT b
		FROM BookmarkLog b
		JOIN FETCH b.user
		JOIN FETCH b.lecture
		WHERE b.id = :id
		"""
	)
	Optional<BookmarkLog> findById(Long id);
}
