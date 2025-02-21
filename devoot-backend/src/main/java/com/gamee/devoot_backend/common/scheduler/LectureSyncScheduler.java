package com.gamee.devoot_backend.common.scheduler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.lecture.document.LectureDocument;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@EnableScheduling
public class LectureSyncScheduler {
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	private LocalDateTime lastSyncTime = LocalDateTime.now();

	@Scheduled(cron = "0 0/5 * * *  ?")
	public void syncLecturesToElasticsearch() {
		log.info("Starting sync. Last sync time: {}", lastSyncTime);

		List<Lecture> changedLectures = lectureRepository.findByUpdatedAtAfter(lastSyncTime);
		changedLectures.forEach(lecture -> log.debug("Changed lecture: {}", lecture));
		//log.info("Found {} changed lectures", changedLectures.size());

		lastSyncTime = LocalDateTime.now();

		List<LectureDocument> documents = changedLectures.stream()
			.map(this::convertToDocument)
			.toList();

		if (!documents.isEmpty()) {
			IndexCoordinates index = IndexCoordinates.of("lectures");
			documents.forEach(document -> {
				elasticsearchOperations.save(document, index);
			});
			//log.info("Synced {} documents to Elasticsearch", documents.size());
			log.info("Synced.");
		} else {
			log.info("No documents to sync.");
		}
	}

	private LectureDocument convertToDocument(Lecture lecture) {
		float rating = (lecture.getReviewCnt() != null && lecture.getReviewCnt() > 0)
			? (lecture.getRatingSum() / lecture.getReviewCnt())
			: 0f;

		List<String> tagsList = Arrays.stream(lecture.getTags().split(","))
			.map(String::trim)
			.map(String::toLowerCase)
			.collect(Collectors.toList());

		return LectureDocument.builder()
			.id(String.valueOf(lecture.getId()))
			.categoryName(lecture.getCategory())
			.name(lecture.getName())
			.lecturer(lecture.getLecturer())
			.tags(tagsList)
			.reviewCnt(lecture.getReviewCnt())
			.currentPrice(lecture.getCurrentPrice())
			.originalPrice(lecture.getOriginalPrice())
			.imageUrl(lecture.getImageUrl())
			.curriculum(lecture.getCurriculum())
			.sourceUrl(lecture.getSourceUrl())
			.sourceName(lecture.getSourceName())
			.createdAt(lecture.getCreatedAt())
			.updatedAt(lecture.getUpdatedAt())
			.hash(lecture.getHash())
			.popularity(rating)
			.build();
	}
}
