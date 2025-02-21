package com.gamee.devoot_backend.lecture.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.lecture.entity.Lecture;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
	@Modifying
	@Query("""
		UPDATE Lecture l
		SET l.reviewCnt = l.reviewCnt + 1,
			l.ratingSum = l.ratingSum + :rating
		WHERE l.id = :id
		""")
	void incrementReviewStats(Long id, Float rating);

	@Modifying
	@Query("""
		UPDATE Lecture l
		SET l.reviewCnt = l.reviewCnt - 1,
			l.ratingSum = l.ratingSum - :rating
		WHERE l.id = :id
		""")
	void decrementReviewStats(Long id, Float rating);

	@Modifying
	@Query("""
		UPDATE Lecture l
		SET l.ratingSum = l.ratingSum - :beforeRating + :newRating
		WHERE l.id = :id
		""")
	void updateReviewStats(Long id, Float beforeRating, Float newRating);

	List<Lecture> findByUpdatedAtAfter(LocalDateTime time);
}
