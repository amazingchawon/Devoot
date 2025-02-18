package com.gamee.devoot_backend.lecture.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamee.devoot_backend.lecture.dto.LectureCreateRequestCreateDto;
import com.gamee.devoot_backend.lecture.dto.LectureCreateRequestDetailDto;
import com.gamee.devoot_backend.lecture.dto.LectureUpdateRequestCreateDto;
import com.gamee.devoot_backend.lecture.dto.LectureUpdateRequestDetailDto;
import com.gamee.devoot_backend.lecture.entity.LectureCreateRequest;
import com.gamee.devoot_backend.lecture.entity.LectureUpdateRequest;
import com.gamee.devoot_backend.lecture.exception.LectureCreateRequestNotFoundException;
import com.gamee.devoot_backend.lecture.exception.LectureUpdateRequestNotFoundException;
import com.gamee.devoot_backend.lecture.repository.LectureCreateRequestRepository;
import com.gamee.devoot_backend.lecture.repository.LectureUpdateRequestRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureRequestService {
	private final LectureCreateRequestRepository createRequestRepository;
	private final LectureUpdateRequestRepository updateRequestRepository;
	private final UserService userService;

	@Transactional
	public void addLectureCreateRequest(CustomUserDetails userDetails, LectureCreateRequestCreateDto dto) {
		createRequestRepository.save(dto.toEntity());
	}

	public List<LectureCreateRequestDetailDto> getLectureCreateRequests(CustomUserDetails userDetails) {
		userService.checkUserIsAdmin(userDetails.id());

		return createRequestRepository.findAll().stream()
			.map(LectureCreateRequestDetailDto::of)
			.toList();
	}

	public void deleteLectureCreateRequest(CustomUserDetails userDetails, Long id) {
		userService.checkUserIsAdmin(userDetails.id());
		LectureCreateRequest request = createRequestRepository.findById(id)
			.orElseThrow(LectureCreateRequestNotFoundException::new);
		createRequestRepository.delete(request);
	}

	public void addLectureUpdateRequest(CustomUserDetails userDetails, LectureUpdateRequestCreateDto dto) {
		updateRequestRepository.save(dto.toEntity());
	}

	public List<LectureUpdateRequestDetailDto> getLectureUpdateRequests(CustomUserDetails userDetails) {
		userService.checkUserIsAdmin(userDetails.id());
		return updateRequestRepository.findAll().stream()
			.map(LectureUpdateRequestDetailDto::of)
			.toList();
	}

	public LectureUpdateRequestDetailDto getLectureUpdateRequestDetail(CustomUserDetails userDetails, Long id) {
		userService.checkUserIsAdmin(userDetails.id());
		LectureUpdateRequest request = updateRequestRepository.findById(id)
			.orElseThrow(LectureUpdateRequestNotFoundException::new);
		return LectureUpdateRequestDetailDto.of(request);
	}

	public void deleteLectureUpdateRequest(CustomUserDetails userDetails, Long id) {
		userService.checkUserIsAdmin(userDetails.id());
		LectureUpdateRequest request = updateRequestRepository.findById(id)
			.orElseThrow(LectureUpdateRequestNotFoundException::new);
		updateRequestRepository.delete(request);
	}
}
