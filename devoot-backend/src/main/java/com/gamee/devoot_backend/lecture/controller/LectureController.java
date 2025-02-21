package com.gamee.devoot_backend.lecture.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.common.enums.CategoryType;
import com.gamee.devoot_backend.common.enums.SortType;
import com.gamee.devoot_backend.common.exception.InvalidEnumException;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecture.dto.LectureCreateDto;
import com.gamee.devoot_backend.lecture.dto.LectureSearchDetailDto;
import com.gamee.devoot_backend.lecture.dto.LectureUpdateDto;
import com.gamee.devoot_backend.lecture.dto.LectureWithBookmarkDetailDto;
import com.gamee.devoot_backend.lecture.service.LectureService;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
@Validated
public class LectureController {
	private final LectureService lectureService;

	@GetMapping("/{lectureId}")
	public ResponseEntity<Map<String, Object>> getLectureDetail(@PathVariable(value = "lectureId") String lectureIdStr,
		@AuthenticationPrincipal CustomUserDetails user) {
		Map<String, Object> resultMap = new HashMap<>();
		LectureWithBookmarkDetailDto lectureWithBookmarkDetailDto = lectureService.getLectureWithBookmarkDetail(Long.parseLong(lectureIdStr), user);
		resultMap.put("lectureDetail", lectureWithBookmarkDetailDto);
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}

	@GetMapping("/{lectureId}/curriculum")
	public ResponseEntity<Map<String, String>> getLectureCurriculum(@PathVariable(value = "lectureId") String lectureIdStr,
		@AuthenticationPrincipal CustomUserDetails user) {
		Map<String, String> resultMap = new HashMap<>();
		LectureWithBookmarkDetailDto lectureWithBookmarkDetailDto = lectureService.getLectureWithBookmarkDetail(Long.parseLong(lectureIdStr), user);
		resultMap.put("curriculum", lectureWithBookmarkDetailDto.curriculum());
		return ResponseEntity.status(HttpStatus.OK).body(resultMap);
	}

	@PostMapping
	public ResponseEntity<?> addLecture(
		@AuthenticationPrincipal CustomUserDetails user,
		@RequestBody LectureCreateDto dto
	) {
		lectureService.addLecture(user, dto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/search")
	public ResponseEntity<?> search(
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "1") @Positive int size,
		@RequestParam(required = false) String category,
		@RequestParam(required = false) String tag,
		@RequestParam(required = false) String sort,
		@RequestParam(required = false) String query
	) {
		try {
			System.out.println("category " + category);
			System.out.println("sort " + sort);
			if (category != null) {
				CategoryType.valueOf(category.replaceAll("[/ ]", ""));
			}
			if (sort != null) {
				SortType.valueOf(sort.toUpperCase());
			}
		} catch (IllegalArgumentException e) {
			throw new InvalidEnumException();
		}

		CustomPage<LectureSearchDetailDto> lectures = lectureService.search(page, size, category, tag, sort, query);
		return ResponseEntity.ok().body(lectures);
	}

	@PatchMapping("/{lectureId}")
	@Transactional
	public ResponseEntity<?> updateLecture(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable Long lectureId,
		@RequestBody LectureUpdateDto dto
	) {
		lectureService.updateLecture(userDetails, lectureId, dto);
		return ResponseEntity.noContent().build();
	}
}
