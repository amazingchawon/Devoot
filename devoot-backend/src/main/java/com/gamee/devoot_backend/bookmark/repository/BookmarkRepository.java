package com.gamee.devoot_backend.bookmark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	@Query("""
		SELECT b
		FROM Bookmark b
		WHERE b.userId = :userId
		AND b.status = :status
		AND b.id NOT IN (
				SELECT b2.nextId
				FROM Bookmark b2
				WHERE b2.userId = :userId
				AND b2.status = :status
				)
		""")
	Optional<Bookmark> findFirstBookmarkOf(Long userId, Integer status);

	@Query("""
		SELECT b
		FROM Bookmark b
		LEFT JOIN b.lecture
		WHERE b.userId = :userId
		""")
	List<Bookmark> findBookmarksByUserId(Long userId);

	Optional<Bookmark> findByUserIdAndLectureId(Long userId, Long lectureId);

	Optional<Bookmark> findByUserIdAndNextId(Long userId, Long id);

	Optional<Bookmark> findByUserIdAndStatusAndNextId(Long userId, Integer status, Long id);

	Long countByLectureId(Long lectureId);
}
