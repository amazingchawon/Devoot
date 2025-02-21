package com.gamee.devoot_backend.todo.repsoitory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.gamee.devoot_backend.todo.entity.TodoContribution;
import com.gamee.devoot_backend.todo.repository.TodoContributionRepository;

@DataJpaTest
@ActiveProfiles("test")
public class TodoContributionRepositoryTest {
	@Autowired
	TodoContributionRepository todoContributionRepository;

	@Test
	@DisplayName("Test findAllByUserIdAndYear()")
	public void testFindAllByUserIdAndYear() {
		// Given
		Integer year = 2020;
		Long userId = 1L;
		LocalDate date1 = LocalDate.of(year, 1, 1);
		LocalDate date2 = LocalDate.of(year, 1, 2);
		LocalDate date3 = LocalDate.of(year + 1, 1, 3);

		TodoContribution todoContribution1 = TodoContribution.builder()
			.userId(userId)
			.date(date1)
			.build();
		TodoContribution todoContribution2 = TodoContribution.builder()
			.userId(userId)
			.date(date2)
			.build();
		TodoContribution todoContribution3 = TodoContribution.builder()
			.userId(userId)
			.date(date3)
			.build();

		todoContribution3.getUserId();
		todoContributionRepository.save(todoContribution1);
		todoContributionRepository.save(todoContribution2);
		todoContributionRepository.save(todoContribution3);

		// When
		List<TodoContribution> todoContributions = todoContributionRepository.findAllByUserIdAndYear(userId, year);

		// Then
		assertEquals(todoContributions.size(), 2);
		assertEquals(todoContributions.get(0).getId(), todoContribution1.getId());
		assertEquals(todoContributions.get(1).getId(), todoContribution2.getId());
	}
}
