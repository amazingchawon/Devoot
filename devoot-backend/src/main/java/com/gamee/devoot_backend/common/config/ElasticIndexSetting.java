package com.gamee.devoot_backend.common.config;

import java.io.File;
import java.io.StringReader;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.lecture.document.LectureDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;

@Configuration
public class ElasticIndexSetting {
	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Autowired
	private ObjectMapper objectMapper;

	@PostConstruct
	public void createIndexWithMapping() throws Exception {
		IndexOperations indexOperations = elasticsearchOperations.indexOps(LectureDocument.class);

		if (!indexOperations.exists()) {
			File file = ResourceUtils.getFile("classpath:es-setting.json");
			var data = objectMapper.readTree(file);
			createIndex(indexOperations.getIndexCoordinates().getIndexName(), data.toString());
			System.out.println("Index created with settings and mappings!");
		} else {
			System.out.println("Index already exists, skipping creation");
		}
	}

	public void createIndex(String indexName, String jsonMapping) throws Exception {
		CreateIndexRequest createIndexRequest = CreateIndexRequest.of(b -> b
			.index(indexName)
			.withJson(new StringReader(jsonMapping))
		);
		CreateIndexResponse response = elasticsearchClient.indices().create(createIndexRequest);
	}
}
