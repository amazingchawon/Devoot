package com.gamee.devoot_backend.bookmark.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
	name = "bookmark",
	uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "lectureId"})
)
public class Bookmark {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lectureId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Lecture lecture;
	private Long lectureId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private User user;
	private Long userId;

	private Integer status;

	@Column(columnDefinition = "bigint default 0")
	private Long nextId;

	@OneToMany(mappedBy = "bookmark", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookmarkLog> bookmarkLogs;
}
