package com.gamee.devoot_backend.todo.repsoitory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.todo.repository.TodoLogRepository;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureJson
public class TodoLogRepositoryTest {
	@Autowired
	private TodoLogRepository todoLogRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Test save()")
	public void testSave1() throws JsonProcessingException {
		// Given
		User user = User.builder().uid("1234").build();
		user = userRepository.save(user);

		Todo todo = Todo.builder()
			.userId(user.getId())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(null)
			.build();
		todo = todoRepository.save(todo);

		TodoLog todoLog = TodoLog.builder()
			.todoId(todo.getId())
			.userId(user.getId())
			.build();

		// When
		todoLogRepository.save(todoLog);

		em.flush();
		em.clear();

		Optional<TodoLog> log = todoLogRepository.findById(todoLog.getId());

		// Then
		assertTrue(log.isPresent());
	}
}
