package com.gamee.devoot_backend.lecture.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.lecture.dto.LectureCreateRequestCreateDto;
import com.gamee.devoot_backend.lecture.dto.LectureUpdateRequestCreateDto;
import com.gamee.devoot_backend.lecture.service.LectureRequestService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lecture-requests")
@RequiredArgsConstructor
@Validated
public class LectureRequestController {
	private final LectureRequestService lectureRequestService;

	@GetMapping("/create")
	public ResponseEntity<?> getLectureCreateRequests(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		return ResponseEntity.ok().body(lectureRequestService.getLectureCreateRequests(userDetails));
	}

	@PostMapping("/create")
	public ResponseEntity<?> addLectureCreateRequest(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody LectureCreateRequestCreateDto dto
	) {
		lectureRequestService.addLectureCreateRequest(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/create/{id}")
	public ResponseEntity<?> deleteLectureCreateRequest(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long id
	) {
		lectureRequestService.deleteLectureCreateRequest(userDetails, id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/update")
	public ResponseEntity<?> getLectureUpdateRequests(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		return ResponseEntity.ok().body(lectureRequestService.getLectureUpdateRequests(userDetails));
	}

	@GetMapping("/update/{id}")
	public ResponseEntity<?> getLectureUpdateRequestDetail(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long id
	) {
		return ResponseEntity.ok().body(lectureRequestService.getLectureUpdateRequestDetail(userDetails, id));
	}

	@PostMapping("/update")
	public ResponseEntity<?> addLectureUpdateRequest(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestBody LectureUpdateRequestCreateDto dto
	) {
		lectureRequestService.addLectureUpdateRequest(dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/update/{id}")
	public ResponseEntity<?> deleteLectureUpdateRequest(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long id
	) {
		lectureRequestService.deleteLectureUpdateRequest(userDetails, id);
		return ResponseEntity.noContent().build();
	}
}
