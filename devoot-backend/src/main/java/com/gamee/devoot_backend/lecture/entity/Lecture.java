package com.gamee.devoot_backend.lecture.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "lecture")
@Entity
@Builder
@Data
public class Lecture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String category;
	private String name;
	private String lecturer;
	private String imageUrl;
	private int originalPrice;
	private int currentPrice;
	private String curriculum;
	private String sourceUrl;
	private String sourceName;
	private String tags;
	private Integer reviewCnt;
	private Float ratingSum;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String hash;
}
