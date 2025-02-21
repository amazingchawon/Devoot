package com.gamee.devoot_backend.lecture.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "lectures")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LectureDocument {
	@Id
	private String id;

	@Field(type = FieldType.Keyword)
	private String categoryName;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "index_analyzer", searchAnalyzer = "search_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "tag_normalizer"),
			@InnerField(suffix = "en", type = FieldType.Text, analyzer = "english_analyzer"),
			@InnerField(suffix = "mixed", type = FieldType.Text, analyzer = "mixed_analyzer")
		}
	)
	private String name;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "index_analyzer", searchAnalyzer = "search_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "tag_normalizer"),
			@InnerField(suffix = "en", type = FieldType.Text, analyzer = "english_analyzer"),
			@InnerField(suffix = "mixed", type = FieldType.Text, analyzer = "mixed_analyzer")
		}
	)
	private String lecturer;

	@MultiField(
		mainField = @Field(type = FieldType.Text, analyzer = "comma_analyzer"),
		otherFields = {
			@InnerField(suffix = "keyword", type = FieldType.Keyword, normalizer = "tag_normalizer"),
			@InnerField(suffix = "split", type = FieldType.Text, analyzer = "comma_analyzer")
		}
	)
	private List<String> tags;

	@Field(type = FieldType.Integer)
	private Integer currentPrice;

	// Non-indexed fields (index = false)
	@Field(type = FieldType.Integer, index = false)
	private Integer categoryId;

	@Field(type = FieldType.Keyword, index = false)
	private String imageUrl;

	@Field(type = FieldType.Integer, index = false)
	private Integer originalPrice;

	@Field(type = FieldType.Keyword, index = false)
	private String curriculum;

	@Field(type = FieldType.Keyword, index = false)
	private String sourceUrl;

	@Field(type = FieldType.Keyword, index = false)
	private String sourceName;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis", index = false)
	private LocalDateTime createdAt;

	@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis", index = false)
	private LocalDateTime updatedAt;

	@Field(type = FieldType.Keyword, index = false)
	private String hash;

	@Field(type = FieldType.Integer, index = false)
	private Integer reviewCnt;

	@Field(type = FieldType.Float)
	private Float popularity;
}
