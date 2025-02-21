package com.gamee.devoot_backend.user.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.user.entity.Admin;
import com.gamee.devoot_backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProfileId(String profileId);

	Optional<User> findByUid(String uid);

	boolean existsByUid(String uid);

	boolean existsByProfileId(String profileId);

	@Query("""
		SELECT u FROM User u
		WHERE u.profileId LIKE :prefix%
		OR u.nickname LIKE :prefix%
		""")
	Page<User> searchByPrefix(String prefix, Pageable pageable);

	@Query("""
		SELECT
			(SELECT COUNT(DISTINCT b) FROM Bookmark b WHERE b.user.id = :userId) AS bookmarkCnt,
			(SELECT COUNT(DISTINCT f) FROM Follow f WHERE f.followerId = :userId AND f.allowed = true) AS followingCnt,
			(SELECT COUNT(DISTINCT f) FROM Follow f WHERE f.followedId = :userId AND f.allowed = true) AS followerCnt
		FROM User u
		WHERE u.id = :userId
		""")
	Map<String, Long> getUserStatsAsMap(Long userId);

	@Query("""
		SELECT
		CASE
			WHEN f.allowed = TRUE THEN 'FOLLOWING'
			WHEN f.allowed = FALSE THEN 'PENDING'
		END
		FROM Follow f
		WHERE f.followerId = :followerId AND f.followedId = :followedId
		""")
	Optional<String> isFollowing(Long followerId, Long followedId);

	@Query("""
		SELECT EXISTS (
			SELECT 1
			FROM Admin a
			WHERE a.userId = :userId
		)
		""")
	Boolean isAdmin(Long userId);

	@Query("""
		SELECT a
		FROM Admin a
		JOIN FETCH a.user
		ORDER BY a.createdAt DESC
		""")
	List<Admin> findAllAdmin();

	@Query("""
		SELECT u
		FROM User u
		JOIN LectureReviewReport r ON r.lectureReview.userId = u.id
		GROUP BY u.id
		HAVING COUNT(r.id) > 3
		""")
	Page<User> findReportedUsers(Long userId, Pageable pageable);
}
