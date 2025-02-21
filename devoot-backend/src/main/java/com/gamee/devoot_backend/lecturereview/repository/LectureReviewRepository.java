package com.gamee.devoot_backend.lecturereview.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.entity.LectureReview;

public interface LectureReviewRepository extends JpaRepository<LectureReview, Long> {
	@Query("""
		SELECT new com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto(lr, u.profileId, u.nickname, u.imageUrl, lt.name, lt.sourceUrl, lt.sourceName)
		FROM LectureReview lr
		JOIN lr.user u
		JOIN lr.lecture lt
		WHERE lt.id = :lectureId
		ORDER BY lr.createdAt DESC
		""")
	Page<LectureReviewDto> selectAllByLectureId(@Param("lectureId") long lectureId, Pageable pageable);

	@Query("""
		SELECT new com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto(lr, u.profileId, u.nickname, u.imageUrl, lt.name, lt.sourceUrl, lt.sourceName)
		FROM LectureReview lr
		JOIN lr.user u
		JOIN lr.lecture lt
		WHERE lr.userId = :userId
		ORDER BY lr.createdAt DESC
		""")
	Page<LectureReviewDto> selectAllByUserId(@Param("userId") long userId, Pageable pageable);

	Optional<LectureReview> findByUserIdAndLectureId(@Param("userId") long userId, @Param("lectureId") long lectureId);

	void deleteByUserId(long userId);
}
