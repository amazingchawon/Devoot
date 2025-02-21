package com.gamee.devoot_backend.lecture.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.bookmark.entity.Bookmark;
import com.gamee.devoot_backend.bookmark.repository.BookmarkRepository;
import com.gamee.devoot_backend.common.enums.SortType;
import com.gamee.devoot_backend.common.enums.TagType;
import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.lecture.document.LectureDocument;
import com.gamee.devoot_backend.lecture.dto.LectureCreateDto;
import com.gamee.devoot_backend.lecture.dto.LectureDetailDto;
import com.gamee.devoot_backend.lecture.dto.LectureSearchDetailDto;
import com.gamee.devoot_backend.lecture.dto.LectureUpdateDto;
import com.gamee.devoot_backend.lecture.dto.LectureWithBookmarkDetailDto;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.lecture.exception.DuplicateLectureException;
import com.gamee.devoot_backend.lecture.exception.LectureNotFoundException;
import com.gamee.devoot_backend.lecture.exception.SearchExecutionErrorException;
import com.gamee.devoot_backend.lecture.repository.LectureCreateRequestRepository;
import com.gamee.devoot_backend.lecture.repository.LectureRepository;
import com.gamee.devoot_backend.lecture.repository.LectureUpdateRequestRepository;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.service.UserService;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.TermsInclude;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {
	private static final List<String> PRESET_TAGS = Arrays.stream(TagType.values())
		.map(TagType::getCanonicalName)
		.collect(Collectors.toList());
	private final LectureRepository lectureRepository;
	private final LectureCreateRequestRepository createRequestRepository;
	private final LectureUpdateRequestRepository updateRequestRepository;
	private final BookmarkRepository bookmarkRepository;
	private final UserService userService;
	private final ElasticsearchOperations elasticsearchOperations;

	public LectureWithBookmarkDetailDto getLectureWithBookmarkDetail(Long id, CustomUserDetails user) {
		Optional<Lecture> lectureOptional = lectureRepository.findById(id);
		if (lectureOptional.isPresent()) {
			Lecture lecture = lectureOptional.get();
			float rating = 0;
			if (lecture.getReviewCnt() != 0) {
				rating = lecture.getRatingSum() / (float)lecture.getReviewCnt();
			}
			long count = bookmarkRepository.countByLectureId(lecture.getId());
			if (user != null) {
				Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByUserIdAndLectureId(user.id(), id);
				if (bookmarkOptional.isPresent()) {
					return new LectureWithBookmarkDetailDto(lecture, count, rating, true, bookmarkOptional.get().getId());
				}
			}
			return new LectureWithBookmarkDetailDto(lecture, count, rating, false, -1);
		}
		throw new LectureNotFoundException();
	}

	public LectureDetailDto getLectureDetail(Long id) {
		Lecture lecture = lectureRepository.findById(id)
			.orElseThrow(LectureNotFoundException::new);
		return LectureDetailDto.of(lecture);
	}

	public CustomPage<LectureSearchDetailDto> search(
		int page,
		int size,
		String category,
		String tag,
		String sort,
		String query
	) {
		List<Query> mustQueries = new ArrayList<>();
		List<Query> filterQueries = new ArrayList<>();
		List<Query> shouldQueries = new ArrayList<>();

		if (query != null && !query.isBlank()) {
			MultiMatchQuery multiMatchQuery = new MultiMatchQuery.Builder()
				.fields(Arrays.asList(
					"name^3",
					"name.mixed^2",
					"name.en^2",
					"lecturer"
				))
				.query(query)
				.type(TextQueryType.BestFields)
				.operator(Operator.Or)
				.minimumShouldMatch("1")
				.build();
			mustQueries.add(multiMatchQuery._toQuery());

			MatchPhraseQuery namePhraseQuery = new MatchPhraseQuery.Builder()
				.field("name")
				.query(query)
				.boost((float)2.0)
				.build();
			shouldQueries.add(namePhraseQuery._toQuery());

			MatchPhraseQuery lecturerPhraseQuery = new MatchPhraseQuery.Builder()
				.field("lecturer")
				.query(query)
				.boost((float)1.5)
				.build();
			shouldQueries.add(lecturerPhraseQuery._toQuery());
		} else if (category != null && !category.isBlank()) {
			TermQuery termQuery = new TermQuery.Builder()
				.field("categoryName")
				.value(category)
				.build();
			filterQueries.add(termQuery._toQuery());
		}

		if (tag != null && !tag.isBlank()) {
			String[] tags = tag.split(",");
			List<Query> tagQueries = Arrays.stream(tags)
				.map(t -> new MatchQuery.Builder()
					.field("tags.keyword")
					.query(t.trim())
					.build()._toQuery())
				.toList();

			filterQueries.add(new BoolQuery.Builder()
				.should(tagQueries)
				.minimumShouldMatch("1")
				.build()._toQuery());
		}

		BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
		if (!mustQueries.isEmpty()) {
			boolQueryBuilder.must(mustQueries);
		}
		if (!shouldQueries.isEmpty()) {
			boolQueryBuilder.should(shouldQueries);
		}
		if (!filterQueries.isEmpty()) {
			boolQueryBuilder.filter(filterQueries);
		}
		if (mustQueries.isEmpty() && filterQueries.isEmpty()) {
			boolQueryBuilder.must(MatchAllQuery.of(m -> m)._toQuery());
		}
		Query finalQuery = boolQueryBuilder.build()._toQuery();

		Sort springSort = getSort(sort);
		NativeQuery nativeQuery = NativeQuery.builder()
			.withQuery(finalQuery)
			.withPageable(PageRequest.of(page - 1, size, springSort))
			.withAggregation("preset_tags", Aggregation.of(a -> a.terms(t -> t
				.field("tags.keyword")
				.include(TermsInclude.of(i -> i.terms(PRESET_TAGS)))
				.size(PRESET_TAGS.size())
			)))
			.build();

		log.info("Executing NativeQuery: {}", nativeQuery);

		SearchHits<LectureDocument> searchHits;
		try {
			searchHits = elasticsearchOperations.search(nativeQuery, LectureDocument.class);
		} catch (Exception e) {
			log.error("Search execution error", e);
			throw new SearchExecutionErrorException();
		}

		List<LectureSearchDetailDto> dtos = searchHits.getSearchHits().stream().map(hit -> {
			LectureDocument doc = hit.getContent();
			return LectureSearchDetailDto.builder()
				.id(Long.valueOf(doc.getId()))
				.category(doc.getCategoryName())
				.tags(String.join(",", doc.getTags()))
				.name(doc.getName())
				.lecturer(doc.getLecturer())
				.currentPrice(doc.getCurrentPrice())
				.originPrice(doc.getOriginalPrice())
				.sourceUrl(doc.getSourceUrl())
				.sourceName(doc.getSourceName())
				.imageUrl(doc.getImageUrl())
				.reviewCnt(doc.getReviewCnt())
				.rating((float)(Math.round(doc.getPopularity() * 10) / 10.0))
				.build();
		}).collect(Collectors.toList());

		Map<String, Object> aggregationsMap = new HashMap<>();
		if (searchHits.getAggregations() == null) {
			log.warn("Elasticsearch aggregations is null");
		} else {
			ElasticsearchAggregations aggs = (ElasticsearchAggregations)searchHits.getAggregations();
			ElasticsearchAggregation tagsAgg = aggs.get("preset_tags");

			if (tagsAgg != null) {
				Aggregate aggregate = tagsAgg.aggregation().getAggregate();

				if (aggregate.isSterms()) {
					StringTermsAggregate stringTerms = aggregate.sterms();
					Map<String, Long> tagCounts = new HashMap<>();

					log.info("Aggregation raw response: {}", stringTerms);

					stringTerms.buckets().array().forEach(bucket -> {
						log.info("Processing bucket: {}", bucket);
						String canonicalKey = bucket.key()._get().toString();
						long docCount = bucket.docCount();
						String displayName = getDisplayNameFromCanonical(canonicalKey);
						tagCounts.put(displayName, docCount);
					});

					aggregationsMap.put("preset_tags", tagCounts);
				}
			}
		}

		return new CustomPage<>(
			new PageImpl<>(dtos, PageRequest.of(page - 1, size, springSort), searchHits.getTotalHits()),
			aggregationsMap
		);
	}

	public void addLecture(CustomUserDetails userDetails, LectureCreateDto dto) {
		userService.checkUserIsAdmin(userDetails.id());
		try {
			createRequestRepository.deleteBySourceUrl(dto.sourceUrl());
			lectureRepository.save(dto.toEntity());
		} catch (DataIntegrityViolationException e) {
			throw new DuplicateLectureException();
		}
	}

	public void updateLecture(CustomUserDetails userDetails, Long id, LectureUpdateDto dto) {
		userService.checkUserIsAdmin(userDetails.id());

		Lecture lecture = lectureRepository.findById(id)
			.orElseThrow(LectureNotFoundException::new);

		dto.updateEntity(lecture);

		updateRequestRepository.deleteByLectureId(id);
		lectureRepository.save(lecture);
	}

	private Sort getSort(String sort) {
		SortType sortType;
		try {
			sortType = (sort == null || sort.isBlank()) ? SortType.RELEVANCE : SortType.valueOf(sort.toUpperCase());
		} catch (IllegalArgumentException e) {
			sortType = SortType.RELEVANCE;
		}
		return switch (sortType) {
			case POPULAR -> Sort.by(Sort.Direction.DESC, "popularity");
			case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
			case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "currentPrice");
			case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "currentPrice");
			default -> Sort.by(Sort.Order.desc("_score"));
		};
	}

	public String getDisplayNameFromCanonical(String canonicalName) {
		return Arrays.stream(TagType.values())
			.filter(tag -> tag.getCanonicalName().equals(canonicalName))
			.findFirst()
			.map(TagType::getDisplayName)
			.orElse(canonicalName);
	}
}
