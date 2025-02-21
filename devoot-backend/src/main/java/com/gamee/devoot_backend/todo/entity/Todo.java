package com.gamee.devoot_backend.todo.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "todo")
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "lectureId", nullable = false)
	private Long lectureId;

	@Temporal(TemporalType.DATE)
	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "lectureName", nullable = false, length = 100)
	private String lectureName;

	@Column(name = "subLectureName", nullable = false, length = 150)
	private String subLectureName;

	@Column(name = "sourceUrl", nullable = false, length = 2083)
	private String sourceUrl;

	@Column(name = "finished", nullable = false)
	private Boolean finished;

	@Column(name = "nextId")
	private Long nextId;

	@OneToMany(mappedBy = "todo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TodoLog> todoLogs;
}
