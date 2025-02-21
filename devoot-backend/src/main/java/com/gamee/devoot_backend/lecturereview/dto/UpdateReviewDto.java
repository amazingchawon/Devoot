package com.gamee.devoot_backend.lecturereview.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record UpdateReviewDto(
	@NotNull(message = "lectureId must not be null.")
	Long lectureId,
	String content,

	@NotNull(message = "rating must not be null.")
	@DecimalMin(value = "0.0", message = "Rating must be at least 1.0.")
	@DecimalMax(value = "5.0", message = "Rating must be at most 5.0.")
	@Digits(integer = 1, fraction = 1, message = "Rating must have at most one decimal place.")
	Float rating
) {
}
