package com.gamee.devoot_backend.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TagsValidator implements ConstraintValidator<ValidTags, String> {
	@Override
	public boolean isValid(String tags,
		ConstraintValidatorContext constraintValidatorContext) {
		if (tags == null || tags.isBlank()) {
			return false;
		}
		String[] tagsArray = tags.split(",");
		if (tagsArray.length < 1 || tagsArray.length > 5) {
			return false;
		}
		for (String tag : tagsArray) {
			if (tag.isBlank()) {
				return false;
			}
		}
		return true;
	}
}
