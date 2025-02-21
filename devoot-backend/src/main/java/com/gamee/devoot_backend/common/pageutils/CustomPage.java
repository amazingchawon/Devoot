package com.gamee.devoot_backend.common.pageutils;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class CustomPage<T> {
	private final long totalElements;
	private final int totalPages;
	private final List<T> content;
	private final Map<String, Object> aggregations;

	public CustomPage(Page<T> page, Map<String, Object> aggregations) {
		content = page.getContent();
		totalElements = page.getTotalElements();
		totalPages = page.getTotalPages();
		this.aggregations = aggregations;
	}

	public CustomPage(Page<T> page) {
		this(page, null);
	}
}
