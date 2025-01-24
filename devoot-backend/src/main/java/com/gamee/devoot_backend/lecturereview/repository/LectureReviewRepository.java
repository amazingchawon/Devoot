package com.gamee.devoot_backend.lecturereview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public interface LectureReviewRepository extends JpaRepository<LectureReview, Long> {
	@Query(value = """
		SELECT AVG(`rating`) FROM `lecturereview` GROUP BY `lectureId` HAVING `lectureId` = :lectureId
		""", nativeQuery = true)
	Float findAvgByLectureId(@Param("lectureId") long lectureId);

	@Query("""
		SELECT new com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto(lr, u.profileId, u.nickname, u.imageUrl)
		FROM LectureReview lr
		JOIN lr.user u
		JOIN lr.lecture lt
		WHERE u.isPublic = true
		AND lt.id = :lectureId
		ORDER BY lr.createdAt DESC
		""")
	Page<LectureReviewDto> selectAllByLectureId(@Param("lectureId") long lectureId, Pageable pageable);

	@Query("""
		SELECT new com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto(lr, u.profileId, u.nickname, u.imageUrl)
		FROM LectureReview lr
		JOIN lr.user u
		JOIN lr.lecture lt
		WHERE lr.userId = :userId
		ORDER BY lr.createdAt DESC
		""")
	Page<LectureReviewDto> selectAllByUserId(@Param("userId") long userId, Pageable pageable);

	@Query("""
		UPDATE LectureReview lr
		SET lr.rating = :rating, lr.content = :content
		WHERE lr.id = :id
		""")
	Optional<LectureReview> update(@Param("id") long id, @Param("rating") float rating, @Param("content") String content);
}
