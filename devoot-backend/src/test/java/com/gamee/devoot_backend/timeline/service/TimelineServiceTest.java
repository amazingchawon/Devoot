package com.gamee.devoot_backend.timeline.service;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.timeline.dto.TimelineLogDetailDto;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;
import com.gamee.devoot_backend.timeline.repository.TimelineLogRepository;
import com.gamee.devoot_backend.todo.entity.Todo;
import com.gamee.devoot_backend.todo.entity.TodoLog;
import com.gamee.devoot_backend.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class TimelineServiceTest {
	@Mock
	TimelineLogRepository timelineLogRepository;

	@InjectMocks
	TimelineService timelineService;

	ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	@DisplayName("Test getTimelineLogs()")
	public void testGetTimelineLogs() throws JsonProcessingException {
		// Given
		User user = User.builder().id(1L).uid("1").profileId("devoot1").nickname("devoot").imageUrl("https://cphinf.pstatic.net/mooc/20201217_218/1608193471491p0oHx_PNG/img_boost_web2.png").build();
		Lecture lecture1 = Lecture.builder().id(1L).build();
		Lecture lecture2 = Lecture.builder().id(2L).build();
		Lecture lecture3 = Lecture.builder().id(3L).build();
		Todo todo1 = Todo.builder()
			.id(11L)
			.userId(user.getId())
			.date(LocalDate.now())
			.lectureId(lecture2.getId())
			.lectureName("Lecture")
			.subLectureName("Sub Lecture")
			.sourceUrl("http://sourceurl.com")
			.finished(true)
			.nextId(null)
			.build();

		List<TimelineLog> logs = new ArrayList<>(List.of(
			BookmarkLog.builder()
				.id(3L)
				.user(user)
				.createdAt(LocalDateTime.now().plusHours(3))
				.beforeStatus(null)
				.afterStatus(1)
				.lecture(lecture1)
				.build(),
			TodoLog.builder()
				.id(4L)
				.user(user)
				.createdAt(LocalDateTime.now().plusHours(2))
				.todo(todo1)
				.build(),
			BookmarkLog.builder()
				.id(9L)
				.user(user)
				.createdAt(LocalDateTime.now().plusHours(1))
				.beforeStatus(1)
				.afterStatus(2)
				.lecture(lecture2)
				.build(),
			BookmarkLog.builder()
				.id(5L)
				.user(user)
				.createdAt(LocalDateTime.now())
				.beforeStatus(2)
				.afterStatus(3)
				.lecture(lecture3)
				.build()
		));

		when(timelineLogRepository.getTimelineLogs(user.getId(), PageRequest.of(0, 10)))
			.thenReturn(new PageImpl<>(logs));

		// When
		CustomPage<TimelineLogDetailDto> logDtos = timelineService.getTimelineLogs(user.getId(), 1, 10);

		// Then
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(logDtos);
		System.out.println(prettyJson);
	}
}
