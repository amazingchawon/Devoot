package com.gamee.devoot_backend.common.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.gamee.devoot_backend.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(DevootException.class)
	public ResponseEntity<Object> handleDevootException(DevootException ex) {
		return ErrorResponse.toResponseEntity(ex);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, List<String>> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error ->
			errors.computeIfAbsent(error.getField(), key -> new ArrayList<>()).add(error.getDefaultMessage())
		);

		return ErrorResponse.toResponseEntity(CommonErrorCode.VALIDATION_FAILED, errors);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		return ErrorResponse.toResponseEntity((HttpStatus)statusCode, ex.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
		Map<String, List<String>> errors = new HashMap<>();

		ex.getConstraintViolations().forEach(violation -> {
			String field = violation.getPropertyPath().toString();
			String message = violation.getMessage();

			errors.computeIfAbsent(field, key -> new ArrayList<>()).add(message);
		});

		return ErrorResponse.toResponseEntity(CommonErrorCode.VALIDATION_FAILED, errors);
	}

	// @ExceptionHandler(Exception.class)
	// public ResponseEntity<Object> handleException(Exception ex) {
	// 	System.out.println("handleException");
	// 	return ErrorResponse.toResponseEntity(CommonErrorCode.INTERNAL_SERVER_ERROR);
	// }
}
