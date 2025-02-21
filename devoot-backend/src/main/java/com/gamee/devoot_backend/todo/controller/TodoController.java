package com.gamee.devoot_backend.todo.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.todo.dto.TodoContributionDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoCreateDto;
import com.gamee.devoot_backend.todo.dto.TodoDetailDto;
import com.gamee.devoot_backend.todo.dto.TodoUpdateDto;
import com.gamee.devoot_backend.todo.service.TodoService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/users/{profileId}/todos")
@RestController
@RequiredArgsConstructor
@Validated
public class TodoController {
	private final TodoService todoService;

	@PostMapping
	public ResponseEntity<?> createTodo(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@RequestBody @Valid TodoCreateDto dto) {
		todoService.createTodo(user, profileId, dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/move-undone")
	public ResponseEntity<?> moveUndone(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@RequestParam(value = "date", required = true) LocalDate date) {
		todoService.moveUndone(user, profileId, date);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<?> getTodos(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@RequestParam(value = "date", required = true) LocalDate date) {
		List<TodoDetailDto> todos = todoService.getTodosOf(user, profileId, date);
		return ResponseEntity.ok().body(todos);
	}

	@GetMapping("/contributions")
	public ResponseEntity<?> getContributions(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@RequestParam(value = "year", required = true) Integer year) {
		List<TodoContributionDetailDto> todoContributions = todoService.getTodoContributionsOf(user, profileId, year);
		return ResponseEntity.ok().body(todoContributions);
	}

	@PatchMapping("/{todoId}/status")
	public ResponseEntity<?> updateTodo(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@PathVariable Long todoId,
		@RequestBody @Valid TodoUpdateDto dto) {
		todoService.updateTodo(user, profileId, todoId, dto);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{todoId}")
	public ResponseEntity<?> deleteTodo(
		@AuthenticationPrincipal CustomUserDetails user,
		@PathVariable String profileId,
		@PathVariable Long todoId) {
		todoService.deleteTodo(user, profileId, todoId);
		return ResponseEntity.noContent().build();
	}
}
