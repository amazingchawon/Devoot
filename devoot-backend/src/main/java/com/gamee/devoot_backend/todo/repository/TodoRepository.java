package com.gamee.devoot_backend.todo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.todo.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.nextId = :nextId
		""")
	Optional<Todo> findByChain(
		@Param("userId") Long userId,
		@Param("date") LocalDate date,
		@Param("nextId") Long nextId
	);

	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		""")
	List<Todo> findTodosOf(Long userId, LocalDate date);

	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.id NOT IN (
			SELECT t2.nextId
			FROM Todo t2
			WHERE t2.userId = :userId
			AND t2.date = :date
		)
		""")
	Optional<Todo> findFirstTodoOf(Long userId, LocalDate date);

	@Query("""
		SELECT t
		FROM Todo t
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.nextId = 0
		""")
	Optional<Todo> findLastTodoOf(Long userId, LocalDate date);

	@Transactional
	@Modifying
	@Query("""
		UPDATE Todo t
		SET t.date = :nextDay
		WHERE t.userId = :userId
		AND t.date = :date
		AND t.finished = false
		""")
	int updateUnfinishedTodosToNextDay(Long userId, LocalDate date, LocalDate nextDay);
}
