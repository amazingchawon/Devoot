package com.gamee.devoot_backend.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gamee.devoot_backend.todo.entity.TodoLog;

public interface TodoLogRepository extends JpaRepository<TodoLog, Long> {
	@Query("""
		SELECT t
		FROM TodoLog t
		JOIN FETCH t.user
		JOIN FETCH t.todo
		WHERE t.id = :id
		"""
	)
	Optional<TodoLog> findById(Long id);
}
