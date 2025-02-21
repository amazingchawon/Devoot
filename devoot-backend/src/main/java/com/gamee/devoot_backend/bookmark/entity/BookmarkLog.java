package com.gamee.devoot_backend.bookmark.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gamee.devoot_backend.bookmark.dto.BookmarkLogDetailDto;
import com.gamee.devoot_backend.lecture.entity.Lecture;
import com.gamee.devoot_backend.timeline.entity.TimelineLog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmarklog")
@DiscriminatorValue("BOOKMARK")
@EqualsAndHashCode(callSuper = true)
public class BookmarkLog extends TimelineLog {
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bookmarkId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Bookmark bookmark;
	private Long bookmarkId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lectureId", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Lecture lecture;
	private long lectureId;

	private Integer beforeStatus;

	private Integer afterStatus;

	@JsonProperty("log")
	@Override
	public BookmarkLogDetailDto getLogData() {
		return BookmarkLogDetailDto.of(this);
	}
}
