package com.gamee.devoot_backend.todo.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.todo.dto.TodoContributionDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoUpdateDto;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.todo.exception.TodoNotFoundException;
import com.gamee.devoot_backend.todo.exception.TodoPermissionDeniedException;
import com.gamee.devoot_backend.todo.repository.TodoContributionRepository;
import com.gamee.devoot_backend.todo.repository.TodoLogRepository;
import com.gamee.devoot_backend.todo.repository.TodoRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	private final TodoRepository todoRepository;
	private final TodoContributionRepository todoContributionRepository;
	private final FollowService followService;
	private final UserService userService;
	private final TodoLogRepository todoLogRepository;
	private final BookmarkRepository bookmarkRepository;

	@Transactional
	public void createTodo(CustomUserDetails user, String profileId, TodoCreateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);

		Todo newTodo = dto.toEntity();
		newTodo.setUserId(user.id());

		todoRepository.findFirstTodoOf(user.id(), newTodo.getDate())
			.ifPresent(
				firstTodo -> {
					newTodo.setNextId(firstTodo.getId());
				});

		todoRepository.save(newTodo);
	}

	@Transactional
	public void moveUndone(CustomUserDetails user, String profileId, LocalDate date) {
		userService.checkUserMatchesProfileId(user, profileId);

		LocalDate nextDay = date.plusDays(1);

		List<Todo> todos = new ArrayList<>();
		List<Todo> finishedTodos = new ArrayList<>();
		List<Todo> unfinishedTodos = new ArrayList<>();

		Map<Long, Todo> todoMap = todoRepository.findTodosOf(user.id(), date).stream()
			.collect(Collectors.toMap(Todo::getId, todo -> todo));

		todoRepository.findFirstTodoOf(user.id(), date)
			.ifPresent(firstUnfinishedTodo ->
				addTodosInOrder(firstUnfinishedTodo, todoMap, todos)
			);

		for (Todo todo : todos) {
			(todo.getFinished() ? finishedTodos : unfinishedTodos).add(todo);
		}

		if (unfinishedTodos.isEmpty()) {
			return;
		}

		updateNextIds(finishedTodos);
		updateNextIds(unfinishedTodos);

		todoRepository.findFirstTodoOf(user.id(), nextDay)
			.ifPresent(todo -> {
				unfinishedTodos.getLast().setNextId(todo.getId());
				todoRepository.save(unfinishedTodos.getLast());
			});

		todoRepository.updateUnfinishedTodosToNextDay(user.id(), date, nextDay);
	}

	public List<TodoDetailDto> getTodosOf(CustomUserDetails user, String profileId, LocalDate date) {
		User followedUser = followService.validateAccessAndFetchFollowedUser(user, profileId);

		List<Todo> todos = new ArrayList<>();

		Map<Long, Todo> todoMap = todoRepository.findTodosOf(followedUser.getId(), date).stream()
			.collect(Collectors.toMap(Todo::getId, todo -> {
				return todo;
			}));

		todoRepository.findFirstTodoOf(followedUser.getId(), date)
			.ifPresent(firstUnfinishedTodo ->
				addTodosInOrder(firstUnfinishedTodo, todoMap, todos)
			);

		return todos.stream()
			.map(TodoDetailDto::of)
			.toList();
	}

	public List<TodoContributionDetailDto> getTodoContributionsOf(CustomUserDetails user, String profileId,
		Integer year) {
		User followedUser = followService.validateAccessAndFetchFollowedUser(user, profileId);

		return todoContributionRepository.findAllByUserIdAndYear(followedUser.getId(), year)
			.stream()
			.map(TodoContributionDetailDto::of)
			.toList();
	}

	@Transactional
	public void updateTodo(CustomUserDetails user, String profileId, Long todoId, TodoUpdateDto dto) {
		userService.checkUserMatchesProfileId(user, profileId);
		Todo todo = checkUserIsAllowedAndFetchTodo(user, todoId);

		Boolean beforeFinished = todo.getFinished();
		Boolean newFinisehd = dto.finished();
		Long beforeNextId = todo.getNextId();
		Long newNextId = dto.nextId();

		// update order
		if (newNextId != -1) {
			todoRepository.findByChain(user.id(), todo.getDate(), todo.getId())
				.ifPresent(beforeTodo -> {
					beforeTodo.setNextId(beforeNextId);
					todoRepository.save(beforeTodo);
				});

			todoRepository.findByChain(user.id(), todo.getDate(), newNextId)
				.ifPresent(newBeforeTodo -> {
					newBeforeTodo.setNextId(todo.getId());
					todoRepository.save(newBeforeTodo);
				});

			todo.setNextId(newNextId);
		}

		todo.setFinished(newFinisehd);

		todoRepository.save(todo);

		// update contribution
		if (!beforeFinished && newFinisehd) {
			todoContributionRepository.insertOrIncrementContribution(user.id(), todo.getDate());
			todoLogRepository.save(
				TodoLog.builder()
					.todoId(todo.getId())
					.userId(user.id())
					.build()
			);
		}
		if (beforeFinished && !newFinisehd) {
			todoContributionRepository.decrementContribution(user.id(), todo.getDate());
			todoContributionRepository.deleteContributionIfZero(user.id(), todo.getDate());
		}
	}

	@Transactional
	public void deleteTodo(CustomUserDetails user, String profileId, Long todoId) {
		userService.checkUserMatchesProfileId(user, profileId);
		Todo todo = checkUserIsAllowedAndFetchTodo(user, todoId);
		if (todo.getFinished()) {
			todoContributionRepository.decrementContribution(user.id(), todo.getDate());
			todoContributionRepository.deleteContributionIfZero(user.id(), todo.getDate());
		}
		todoRepository.findByChain(user.id(), todo.getDate(), todo.getId())
			.ifPresent(beforeTodo -> {
				beforeTodo.setNextId(todo.getNextId());
				todoRepository.save(beforeTodo);
			});
		todoRepository.delete(todo);
	}

	private void addTodosInOrder(Todo startTodo, Map<Long, Todo> todoMap, List<Todo> todos) {
		Todo currentTodo = startTodo;
		do {
			todos.add(currentTodo);
			currentTodo = todoMap.get(currentTodo.getNextId());
		} while (!Objects.equals(currentTodo, null));
	}

	private Todo checkUserIsAllowedAndFetchTodo(CustomUserDetails user, Long todoId) {
		Todo todo = todoRepository.findById(todoId)
			.orElseThrow(TodoNotFoundException::new);
		if (!todo.getUserId().equals(user.id())) {
			throw new TodoPermissionDeniedException();
		}
		return todo;
	}

	private void updateNextIds(List<Todo> todos) {
		if (todos.size() < 2) {
			return;
		}

		ListIterator<Todo> iterator = todos.listIterator();
		Todo prev = iterator.next();
		while (iterator.hasNext()) {
			Todo curr = iterator.next();
			prev.setNextId(curr.getId());
			todoRepository.save(prev);
			prev = curr;
		}

		prev.setNextId(0L);
		todoRepository.save(prev);
	}
}
