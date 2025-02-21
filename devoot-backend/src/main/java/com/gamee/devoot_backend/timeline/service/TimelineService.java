package com.gamee.devoot_backend.timeline.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.timeline.dto.TimelineLogDetailDto;
import com.gamee.devoot_backend.timeline.repository.TimelineLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimelineService {
	private final TimelineLogRepository timelineLogRepository;

	public CustomPage<TimelineLogDetailDto> getTimelineLogs(Long userId, Integer page, Integer size) {
		return new CustomPage<>(
			timelineLogRepository.getTimelineLogs(userId, PageRequest.of(page - 1, size))
				.map(TimelineLogDetailDto::of)
		);
	}
}
