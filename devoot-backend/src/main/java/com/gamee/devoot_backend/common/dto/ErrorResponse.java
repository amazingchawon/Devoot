package com.gamee.devoot_backend.common.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.common.exception.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
	private String code;
	private String message;
	private String detail;
	private Map<String, List<String>> errors;

	public static ResponseEntity<Object> toResponseEntity(DevootException exception) {
		ErrorCode errorCode = exception.getErrorCode();
		String detail = exception.getDetail();

		return ResponseEntity
			.status(errorCode.getStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(
				ErrorResponse.builder()
					.code(errorCode.getCode())
					.message(errorCode.getMessage())
					.detail(detail)
					.build()
			);
	}

	public static ResponseEntity<Object> toResponseEntity(ErrorCode errorCode) {
		return ResponseEntity
			.status(errorCode.getStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(
				ErrorResponse.builder()
					.code(errorCode.getCode())
					.message(errorCode.getMessage())
					.build()
			);
	}

	public static ResponseEntity<Object> toResponseEntity(HttpStatus status, String detail) {
		return ResponseEntity
			.status(status)
			.contentType(MediaType.APPLICATION_JSON)
			.body(
				ErrorResponse.builder()
					.code(String.format("COMMON_%s", status.value()))
					.message(status.getReasonPhrase())
					.detail(detail)
					.build()
			);
	}

	public static ResponseEntity<Object> toResponseEntity(ErrorCode errorCode, Map<String, List<String>> errors) {
		Map<String, String> message = new HashMap<>();
		message.put("code", errorCode.getCode());

		return ResponseEntity
			.status(errorCode.getStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(
				ErrorResponse.builder()
					.code(errorCode.getCode())
					.message(errorCode.getMessage())
					.errors(errors)
					.build()
			);
	}

}
