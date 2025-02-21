package com.gamee.devoot_backend.todo.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoUpdateDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoContribution;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.todo.exception.TodoNotFoundException;
import com.gamee.devoot_backend.todo.exception.TodoPermissionDeniedException;
import com.gamee.devoot_backend.todo.repository.TodoContributionRepository;
import com.gamee.devoot_backend.todo.repository.TodoLogRepository;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
	@Mock
	TodoRepository todoRepository;

	@Mock
	TodoLogRepository todoLogRepository;

	@Mock
	TodoContributionRepository todoContributionRepository;

	@Mock
	UserService userService;

	@Mock
	FollowService followService;

	@InjectMocks
	TodoService todoService;

	CustomUserDetails user = CustomUserDetails.builder()
		.id(1L)
		.profileId("testProfileId")
		.build();

	TodoCreateDto createDto = new TodoCreateDto(1L, LocalDate.now(), "lecture", "sublecture", "www.hello.com", false);

	@Test
	@DisplayName("Test createTodo() - when there are no todos")
	public void testCreateTodo1() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());

		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(todoRepository.findFirstTodoOf(user.id(), createDto.date()))
			.thenReturn(Optional.empty());

		// When
		todoService.createTodo(user, user.profileId(), createDto);

		// Then
		verify(todoRepository, times(1)).save(newTodo);
	}

	@Test
	@DisplayName("Test createTodo() - when todos are found")
	public void testCreateTodo2() {
		// Given
		Todo newTodo = createDto.toEntity();
		newTodo.setUserId(user.id());

		Todo firstTodo = Todo.builder()
			.id(2L)
			.userId(user.id())
			.nextId(0L)
			.build();

		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(todoRepository.findFirstTodoOf(user.id(), newTodo.getDate()))
			.thenReturn(Optional.of(firstTodo));

		// When
		todoService.createTodo(user, user.profileId(), createDto);

		// Then
		ArgumentCaptor<Todo> todoCaptor = ArgumentCaptor.forClass(Todo.class);
		verify(todoRepository, times(1)).save(todoCaptor.capture());

		Todo savedTodo = todoCaptor.getValue();
		assertEquals(savedTodo.getNextId(), firstTodo.getId());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are no todos to move")
	public void testMoveUndone1() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = date.plusDays(1);

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

		Todo todo1 = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(2L)
			.build();
		Todo todo2 = Todo.builder()
			.id(2L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(3L)
			.build();
		Todo todo3 = Todo.builder()
			.id(3L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(4L)
			.build();
		Todo todo4 = Todo.builder()
			.id(4L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);
		todoRepository.save(todo4);

		// Given
		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(todoRepository.findTodosOf(user.id(), date))
			.thenReturn(List.of(todo1, todo2, todo3, todo4));
		when(todoRepository.findFirstTodoOf(user.id(), date))
			.thenReturn(Optional.of(todo1));

		// When
		todoService.moveUndone(user, user.profileId(), date);

		// Then
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
		assertEquals(todo1.getNextId(), todo3.getId());
		assertEquals(todo2.getNextId(), todo4.getId());
		assertEquals(todo3.getNextId(), 0);
		assertEquals(todo4.getNextId(), 0);

		ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(eq(user.id()), eq(date), dateCaptor.capture());

		assertEquals(nextDay, dateCaptor.getValue());
	}

	@Test
	@DisplayName("Test moveUndone() - when there are todos to move and todos of next day")
	public void testMoveUndone3() {
		LocalDate date = LocalDate.now();
		LocalDate nextDay = LocalDate.now().plusDays(1);

		Todo todo1 = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(2L)
			.build();
		Todo todo2 = Todo.builder()
			.id(2L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(3L)
			.build();
		Todo todo3 = Todo.builder()
			.id(3L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(0L)
			.build();
		Todo todo4 = Todo.builder()
			.id(4L)
			.userId(user.id())
			.date(nextDay)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);
		todoRepository.save(todo4);

		// Given
		doNothing().when(userService).checkUserMatchesProfileId(user, user.profileId());
		when(todoRepository.findTodosOf(user.id(), date))
			.thenReturn(List.of(todo1, todo2, todo3, todo4));
		when(todoRepository.findFirstTodoOf(user.id(), date))
			.thenReturn(Optional.of(todo1));
		when(todoRepository.findFirstTodoOf(user.id(), nextDay))
			.thenReturn(Optional.of(todo4));

		// When
		todoService.moveUndone(user, user.profileId(), date);

		// Then
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
		assertEquals(todo1.getNextId(), todo3.getId());
		assertEquals(todo2.getNextId(), todo4.getId());
		assertEquals(todo3.getNextId(), 0);

		ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
		verify(todoRepository, times(1)).updateUnfinishedTodosToNextDay(eq(user.id()), eq(date), dateCaptor.capture());

		assertEquals(nextDay, dateCaptor.getValue());
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

		Todo secondFinishedTodo = Todo.builder()
			.id(2L)
			.userId(user.id())
			.date(date)
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(0L)
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
			.nextId(firstFinishedTodo.getId())
			.build();

		when(followService.validateAccessAndFetchFollowedUser(user, diffProfileId))
			.thenReturn(diffUser);
		when(todoRepository.findTodosOf(diffUser.getId(), date))
			.thenReturn(List.of(firstFinishedTodo, secondFinishedTodo, firstUnfinishedTodo));
		when(todoRepository.findFirstTodoOf(diffUser.getId(), date))
			.thenReturn(Optional.of(firstUnfinishedTodo));

		// When
		List<TodoDetailDto> todos = todoService.getTodosOf(user, diffProfileId, date);

		// Then
		verify(todoRepository, times(1)).findTodosOf(diffUser.getId(), date);
		verify(todoRepository, times(1)).findFirstTodoOf(diffUser.getId(), date);

		assertEquals(todos.size(), 3);
		assertEquals(todos.get(0).id(), firstUnfinishedTodo.getId());
		assertEquals(todos.get(1).id(), firstFinishedTodo.getId());
		assertEquals(todos.get(2).id(), secondFinishedTodo.getId());

	}

	@Test
	@DisplayName("Test checkUserIsAllowedAndFetchTodo() - throw TodoNotFoundException")
	public void testCheckUserIsAllowedAndFetchTodo1() {
		// When & Then
		assertThatThrownBy(() -> todoService.updateTodo(user, user.profileId(), 1L, null))
			.isInstanceOf(TodoNotFoundException.class);
	}

	@Test
	@DisplayName("Test checkUserIsAllowedAndFetchTodo() - throw TodoPermissionDeniedException")
	public void testCheckUserIsAllowedAndFetchTodo2() {
		// Given
		Todo todo = Todo.builder()
			.id(2L)
			.userId(user.id() + 1)
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(0L)
			.build();

		when(todoRepository.findById(todo.getId()))
			.thenReturn(Optional.of(todo));

		// When & Then
		assertThatThrownBy(() -> todoService.updateTodo(user, user.profileId(), todo.getId(), null))
			.isInstanceOf(TodoPermissionDeniedException.class);
	}

	@Test
	@DisplayName("Test updateTodo() - when only order changed")
	public void testUpdateTodo1() {
		// Given
		Todo todo1 = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(2L)
			.build();
		Todo todo2 = Todo.builder()
			.id(2L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(3L)
			.build();
		Todo todo3 = Todo.builder()
			.id(3L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(0L)
			.build();
		todoRepository.save(todo1);
		todoRepository.save(todo2);
		todoRepository.save(todo3);

		TodoUpdateDto dto = new TodoUpdateDto(!todo1.getFinished(), todo3.getId());

		when(todoRepository.findById(todo1.getId()))
			.thenReturn(Optional.of(todo1));
		when(todoRepository.findByChain(user.id(), todo1.getDate(), todo1.getId()))
			.thenReturn(Optional.empty());
		when(todoRepository.findByChain(user.id(), todo1.getDate(), dto.nextId()))
			.thenReturn(Optional.of(todo2));

		// When :  1 - 2 - 3 -> 2 - 1 - 3
		todoService.updateTodo(user, user.profileId(), todo1.getId(), dto);

		// Then
		assertEquals(todo2.getNextId(), todo1.getId());
		assertEquals(todo1.getNextId(), todo3.getId());
		assertEquals(0L, todo3.getNextId());
	}

	@Test
	@DisplayName("Test updateTodo() - unfinished -> finished")
	public void testUpdateTodo2() {
		// Given
		Todo todo = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(2L)
			.build();
		TodoContribution todoContribution = TodoContribution.builder()
			.userId(todo.getUserId())
			.cnt(1)
			.date(todo.getDate())
			.build();

		todoRepository.save(todo);
		todoContributionRepository.save(todoContribution);

		TodoUpdateDto dto = new TodoUpdateDto(true, -1L);

		when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

		// When
		todoService.updateTodo(user, user.profileId(), todo.getId(), dto);

		// Then
		verify(todoRepository, never()).findByChain(any(), any(), any());
		verify(todoContributionRepository).insertOrIncrementContribution(todo.getUserId(), todo.getDate());
		verify(todoContributionRepository, never()).decrementContribution(anyLong(), any());
		verify(todoContributionRepository, never()).deleteContributionIfZero(anyLong(), any());
		verify(todoLogRepository).save(any(TodoLog.class));
	}

	@Test
	@DisplayName("Test updateTodo() - finished -> unfinished")
	public void testUpdateTodo3() {
		// Given
		Todo todo = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(2L)
			.build();
		TodoContribution todoContribution = TodoContribution.builder()
			.userId(todo.getUserId())
			.cnt(1)
			.date(todo.getDate())
			.build();

		todoRepository.save(todo);
		todoContributionRepository.save(todoContribution);

		TodoUpdateDto dto = new TodoUpdateDto(false, -1L);

		when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

		// When
		todoService.updateTodo(user, user.profileId(), todo.getId(), dto);

		// Then
		verify(todoRepository, never()).findByChain(any(), any(), any());
		verify(todoContributionRepository, never()).insertOrIncrementContribution(anyLong(), any());
		verify(todoContributionRepository).decrementContribution(todo.getUserId(), todo.getDate());
		verify(todoContributionRepository).deleteContributionIfZero(todo.getUserId(), todo.getDate());
		verifyNoInteractions(todoLogRepository);
	}

	@Test
	@DisplayName("Test deleteTodo - when unfinished")
	public void testDeleteTodo1() {
		// Given
		Todo todo = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(false)
			.nextId(2L)
			.build();
		TodoContribution todoContribution = TodoContribution.builder()
			.userId(todo.getUserId())
			.cnt(1)
			.date(todo.getDate())
			.build();

		todoRepository.save(todo);
		todoContributionRepository.save(todoContribution);

		when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

		// When
		todoService.deleteTodo(user, user.profileId(), todo.getId());

		// Then
		verify(todoRepository).delete(todo);
		verify(todoContributionRepository, never()).decrementContribution(any(), any());
		verify(todoContributionRepository, never()).deleteContributionIfZero(any(), any());
	}

	@Test
	@DisplayName("Test deleteTodo - when finished")
	public void testDeleteTodo2() {
		// Given
		Todo todo = Todo.builder()
			.id(1L)
			.userId(user.id())
			.date(LocalDate.now())
			.lectureId(2L)
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.finished(true)
			.nextId(2L)
			.build();
		TodoContribution todoContribution = TodoContribution.builder()
			.userId(todo.getUserId())
			.cnt(1)
			.date(todo.getDate())
			.build();

		todoRepository.save(todo);
		todoContributionRepository.save(todoContribution);

		when(todoRepository.findById(todo.getId())).thenReturn(Optional.of(todo));

		// When
		todoService.deleteTodo(user, user.profileId(), todo.getId());

		// Then
		verify(todoRepository).delete(todo);
		verify(todoContributionRepository).decrementContribution(todo.getUserId(), todo.getDate());
		verify(todoContributionRepository).deleteContributionIfZero(todo.getUserId(), todo.getDate());
	}
}
