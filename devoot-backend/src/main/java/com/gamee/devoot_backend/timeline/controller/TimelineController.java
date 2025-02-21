package com.gamee.devoot_backend.timeline.controller;

import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.timeline.dto.TimelineLogDetailDto;
import com.gamee.devoot_backend.timeline.service.TimelineService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timeline")
@Validated
public class TimelineController {
	private final TimelineService timelineService;

	@GetMapping
	public ResponseEntity<?> getTimeline(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "1") @Positive int size
	) {
		CustomPage<TimelineLogDetailDto> logs = timelineService.getTimelineLogs(userDetails.id(), page, size);
		return ResponseEntity.ok(logs);
	}
}
