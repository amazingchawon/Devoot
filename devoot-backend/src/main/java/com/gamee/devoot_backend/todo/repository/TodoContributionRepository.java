package com.gamee.devoot_backend.todo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.todo.entity.TodoContribution;

public interface TodoContributionRepository extends JpaRepository<TodoContribution, Long> {
	@Query("""
		SELECT t
		FROM TodoContribution t
		WHERE t.userId = :userId
		AND YEAR(t.date) = :year
		ORDER BY t.date ASC
		""")
	List<TodoContribution> findAllByUserIdAndYear(Long userId, Integer year);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = """
		INSERT INTO todocontribution (`userId`, `date`, `cnt`)
		VALUES (:userId, :date, 1)
		ON DUPLICATE KEY UPDATE `cnt` = `cnt` + 1;
		""", nativeQuery = true)
	void insertOrIncrementContribution(@Param("userId") Long userId, @Param("date") LocalDate date);

	@Modifying
	@Query("""
		UPDATE TodoContribution t
		SET t.cnt = t.cnt - 1
		WHERE t.userId = :userId AND t.date = :date
		""")
	void decrementContribution(Long userId, LocalDate date);

	@Modifying
	@Query("""
		DELETE FROM TodoContribution t
		WHERE t.userId = :userId AND t.date = :date AND t.cnt <= 0
		""")
	void deleteContributionIfZero(Long userId, LocalDate date);
}
