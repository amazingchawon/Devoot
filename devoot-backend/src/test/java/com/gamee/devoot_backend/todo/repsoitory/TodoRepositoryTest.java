package com.gamee.devoot_backend.todo.repsoitory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoRepository;

@DataJpaTest
@ActiveProfiles("test")
public class TodoRepositoryTest {
	@Autowired
	TodoRepository todoRepository;

	@Autowired
	EntityManager em;

	@Test
	@DisplayName("Test findByChain()")
	public void testFindByChain() {
		// Given
		Todo todo = Todo.builder()
			.userId(1L)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.date(LocalDate.now())
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		todoRepository.save(todo);

		// When
		Optional<Todo> foundTodo = todoRepository.findByChain(1L, todo.getDate(), 0L);

		// Then
		assertTrue(foundTodo.isPresent());
	}

	@Test
	@DisplayName("Test updateUnfinishedTodosToNextDay()")
	public void testUpdateUnfinishedTodosToNextDay() {
		// When
		Long userId = 1L;
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		Todo unfinishedTodo1 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo unfinishedTodo2 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo finishedTodo1 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(0L)
			.build();
		todoRepository.save(unfinishedTodo1);
		todoRepository.save(unfinishedTodo2);
		todoRepository.save(finishedTodo1);

		// When
		int res = todoRepository.updateUnfinishedTodosToNextDay(userId, date, nextDay);
		em.clear();

		// Then
		assertEquals(2, res);
		todoRepository.findById(unfinishedTodo1.getId())
			.ifPresent(todo -> assertEquals(nextDay, todo.getDate()));
		todoRepository.findById(unfinishedTodo2.getId())
			.ifPresent(todo -> assertEquals(nextDay, todo.getDate()));
		todoRepository.findById(finishedTodo1.getId())
			.ifPresent(todo -> assertEquals(date, todo.getDate()));
	}

	@Test
	@DisplayName("Test findFirstTodoOf()")
	public void testFindFirstTodoOf() {
		// When
		Long userId = 1L;
		LocalDate date = LocalDate.now();

		Todo todo1 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo todo2 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo todo3 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);

		todo1.setNextId(todo2.getId());
		todo2.setNextId(todo3.getId());
		todoRepository.save(todo1);
		todoRepository.save(todo2);

		// When
		Optional<Todo> todoOptional = todoRepository.findFirstTodoOf(userId, date);

		// Then
		assertTrue(todoOptional.isPresent());
		assertEquals(todoOptional.get().getId(), todo1.getId());
	}

	@Test
	@DisplayName("Test findLastTodoOf()")
	public void testFindLastTodoOf() {
		// When
		Long userId = 1L;
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		Todo todo1 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo todo2 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo todo3 = Todo.builder()
			.userId(userId)
			.date(nextDay)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);

		todo1.setNextId(todo2.getId());
		todoRepository.save(todo1);

		// When
		Optional<Todo> todoOptional = todoRepository.findLastTodoOf(userId, date);

		// Then
		assertTrue(todoOptional.isPresent());
		assertEquals(todoOptional.get().getId(), todo2.getId());
	}

	@Test
	@DisplayName("Test findTodosOf()")
	public void testFindTodosOf() {
		// When
		Long userId = 1L;
		Long diffUserId = 2L;
		LocalDate date = LocalDate.now();
		LocalDate diffDay = date.plusDays(1);

		Todo todo1 = Todo.builder()
			.userId(userId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(false)
			.nextId(0L)
			.build();
		Todo todo2 = Todo.builder()
			.userId(diffUserId)
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(0L)
			.build();
		Todo todo3 = Todo.builder()
			.userId(userId)
			.date(diffDay)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);

		// When
		List<Todo> todos = todoRepository.findTodosOf(userId, date);

		// Then
		assertNotNull(todos);
		assertEquals(1, todos.size());
		assertEquals(todo1.getId(), todos.getFirst().getId());
	}
}
