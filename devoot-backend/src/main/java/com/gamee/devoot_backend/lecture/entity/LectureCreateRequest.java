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
@Table(name = "lecturecreaterequest")
@Entity
@Builder
@Data
public class LectureCreateRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String sourceUrl;

	private LocalDateTime createdAt;

	private String hash;
}
