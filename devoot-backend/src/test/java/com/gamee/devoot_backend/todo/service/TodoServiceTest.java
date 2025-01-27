package com.gamee.devoot_backend.todo.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.follow.entity.Follow;
import com.gamee.devoot_backend.follow.exception.FollowRequestPendingException;
import com.gamee.devoot_backend.follow.repository.FollowRepository;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dao.UserRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserNotFoundException;
import com.gamee.devoot_backend.user.exception.UserProfileIdMismatchException;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
	@Mock
	TodoRepository todoRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	FollowRepository followRepository;

	@InjectMocks
	TodoService todoService;

	CustomUserDetails user = new CustomUserDetails(1L, "testProfileId", null, null, true);

	TodoCreateDto createDto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture", "www.hello.com", false);

	@Test
	@DisplayName("Test createTodo() - when there is no before todos")
	public void testCreateTodo1() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());
		when(todoRepository.findByUserIdAndFinishedAndNextId(user.id(), false, null))
			.thenReturn(Optional.empty());

		// When
		todoService.createTodo(user, user.profileId(), createDto);

		// Then
		verify(todoRepository).save(newTodo);
		verify(todoRepository, times(1)).save(any(Todo.class));
	}

	@Test
	@DisplayName("Test createTodo() - when before todos found")
	public void testCreateTodo2() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());

		Todo beforeTodo = Todo.builder()
			.userId(user.id())
			.finished(createDto.finished())
			.nextId(null)
			.build();

		when(todoRepository.findByUserIdAndFinishedAndNextId(user.id(), createDto.finished(), null))
			.thenReturn(Optional.of(beforeTodo));

		// When
		todoService.createTodo(user, user.profileId(), createDto);

		// Then
		verify(todoRepository).save(newTodo);
		verify(todoRepository).save(beforeTodo);
		verify(todoRepository, times(2)).save(any(Todo.class));

		assertEquals(newTodo.getId(), beforeTodo.getNextId());
	}

	@Test
	@DisplayName("Test createTodo() - throw exception when profile id doesn't match")
	public void testCreateTodo3() {
		// Given
		String diffProfileId = "diffProfileId";

		// When & Then
		assertThatThrownBy(() -> todoService.createTodo(user, diffProfileId, createDto))
			.isInstanceOf(UserProfileIdMismatchException.class)
			.hasMessage(null);
		verify(todoRepository, never()).save(any());
		verify(todoRepository, never()).findByUserIdAndFinishedAndNextId(any(), any(), any());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are no todos to move")
	public void testMoveUndone1() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.empty());
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.empty());

		// When
		todoService.moveUndone(user, user.profileId(), date);

		// Then
		verify(todoRepository, never()).save(any());
		verify(todoRepository, never()).updateUnfinishedTodosToNextDay(anyLong(), any(), any());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are todos to move, but no todos on the next day")
	public void testMoveUndone2() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

		Todo lastNewTodo = Todo.builder()
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.of(lastNewTodo));
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.empty());

		// When
		todoService.moveUndone(user, user.profileId(), date);

		// Then
		verify(todoRepository, never()).save(any());
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
	}

	@Test
	@DisplayName("Test moveUndone() - when there are todos to move and todos of next day")
	public void testMoveUndone3() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = LocalDate.now().plusDays(1);

		Todo lastNewTodo = Todo.builder()
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();
		Todo firstExistingTodo = Todo.builder()
			.userId(user.id())
			.date(nextDay)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();

		// Given
		when(todoRepository.findLastTodoOf(user.id(), date, false))
			.thenReturn(Optional.of(lastNewTodo));
		when(todoRepository.findFirstTodoOf(user.id(), nextDay, false))
			.thenReturn(Optional.of(lastNewTodo));

		// When
		todoService.moveUndone(user, user.profileId(), date);

		// Then
		assertEquals(firstExistingTodo.getId(), lastNewTodo.getNextId());
		verify(todoRepository).save(lastNewTodo);
		verify(todoRepository).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
	}

	@Test
	@DisplayName("Test moveUndone() - when there are no todos to move")
	public void testMoveUndone4() {
		// Given
		String diffProfileId = "diffProfileId";
		LocalDate date = LocalDate.now();

		// When & Then
		assertThatThrownBy(() -> todoService.moveUndone(user, diffProfileId, date))
			.isInstanceOf(UserProfileIdMismatchException.class)
			.hasMessage(null);

		verify(todoRepository, never()).save(any());
		verify(todoRepository, never()).findLastTodoOf(any(), any(), any());
		verify(todoRepository, never()).findFirstTodoOf(any(), any(), any());
		verify(todoRepository, never()).updateUnfinishedTodosToNextDay(any(), any(), any());
	}

	@Test
	@DisplayName("Test getTodosOf() - when both finished and unfinished todos exist")
	public void testGetTodosOf1() {
		// Given
		LocalDate date = LocalDate.now();
		String diffProfileId = "diffProfileId";

		User diffUser = User.builder()
			.id(2L)
			.profileId(diffProfileId)
			.isPublic(false)
			.build();
		Follow follow = Follow.builder()
			.build();

		Todo secondFinishedTodo = Todo.builder()
			.id(2L)
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(null)
			.build();
		Todo firstFinishedTodo = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(secondFinishedTodo.getId())
			.build();
		Todo firstUnfinishedTodo = Todo.builder()
			.id(3L)
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(null)
			.build();

		when(userRepository.findByProfileId(diffProfileId))
			.thenReturn(Optional.of(diffUser));
		when(followRepository.findIfAllowed(user.id(), diffUser.getId()))
			.thenReturn(Optional.of(follow));
		when(todoRepository.findTodosOf(user.id(), date))
			.thenReturn(List.of(firstFinishedTodo, secondFinishedTodo, firstUnfinishedTodo));
		when(todoRepository.findFirstTodoOf(user.id(), date, false))
			.thenReturn(Optional.of(firstUnfinishedTodo));
		when(todoRepository.findFirstTodoOf(user.id(), date, true))
			.thenReturn(Optional.of(firstFinishedTodo));

		// When
		List<TodoDetailDto> todos = todoService.getTodosOf(user, diffProfileId, date);

		// Then
		verify(todoRepository, times(1)).findTodosOf(user.id(), date);
		verify(todoRepository, times(1)).findFirstTodoOf(user.id(), date, true);
		verify(todoRepository, times(1)).findFirstTodoOf(user.id(), date, false);

		assertEquals(todos.size(), 3);
		assertEquals(todos.get(0).id(), firstUnfinishedTodo.getId());
		assertEquals(todos.get(1).id(), firstFinishedTodo.getId());
		assertEquals(todos.get(2).id(), secondFinishedTodo.getId());

	}

	@Test
	@DisplayName("Test getTodosOf() - when user of profile id doesn't exist")
	public void testGetTodosOf2() {
		// Given
		String nonExistantProfileId = "nonExistantProfileId";
		LocalDate date = LocalDate.now();

		when(userRepository.findByProfileId(nonExistantProfileId))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> todoService.getTodosOf(user, nonExistantProfileId, date))
			.isInstanceOf(UserNotFoundException.class)
			.extracting("detail")
			.isEqualTo(String.format("User of %s not found", nonExistantProfileId));

		verify(todoRepository, never()).findTodosOf(any(), any());
		verify(todoRepository, never()).findFirstTodoOf(any(), any(), any());
	}

	@Test
	@DisplayName("Test getTodosOf() - throw exception when user's follow request is pending")
	public void testGetTodosOf3() {
		// Given
		String diffProfileId = "diffProfileId";
		LocalDate date = LocalDate.now();

		User diffUser = User.builder()
			.id(2L)
			.profileId(diffProfileId)
			.isPublic(false)
			.build();

		when(userRepository.findByProfileId(diffProfileId))
			.thenReturn(Optional.of(diffUser));
		when(followRepository.findIfAllowed(user.id(), diffUser.getId()))
			.thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> todoService.getTodosOf(user, diffProfileId, date))
			.isInstanceOf(FollowRequestPendingException.class);

		verify(todoRepository, never()).findTodosOf(any(), any());
		verify(todoRepository, never()).findFirstTodoOf(any(), any(), any());
	}
}
