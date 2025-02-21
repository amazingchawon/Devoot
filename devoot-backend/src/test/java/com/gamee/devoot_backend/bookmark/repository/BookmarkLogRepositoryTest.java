package com.gamee.devoot_backend.bookmark.repository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.bookmark.entity.BookmarkLog;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureJson
public class BookmarkLogRepositoryTest {
	@Autowired
	private BookmarkLogRepository bookmarkLogRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("Test save()")
	public void testSave1() throws JsonProcessingException {
		// Given
		User user = User.builder().uid("1234").build();
		Lecture lecture = Lecture.builder().build();

		user = userRepository.save(user);
		lecture = lectureRepository.save(lecture);

		BookmarkLog bookmarkLog = BookmarkLog.builder()
			.beforeStatus(null)
			.afterStatus(1)
			.lectureId(lecture.getId())
			.userId(user.getId())
			.build();

		// When
		bookmarkLogRepository.save(bookmarkLog);

		em.flush();
		em.clear();

		bookmarkLog = bookmarkLogRepository.findById(bookmarkLog.getId()).get();

		// Then
		String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookmarkLog);
		System.out.println(prettyJson);
	}
}
