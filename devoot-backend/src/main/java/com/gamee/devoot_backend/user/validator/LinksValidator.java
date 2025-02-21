package com.gamee.devoot_backend.user.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LinksValidator implements ConstraintValidator<ValidLinks, String> {

	@Override
	public boolean isValid(String links,
		ConstraintValidatorContext constraintValidatorContext) {
		if (links == null) {
			setCustomMessage(constraintValidatorContext, "links cannot be null");
			return false;
		}
		if (links.trim().isEmpty()) {
			return true;
		}
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(links.trim());

			if (links.trim().length() != rootNode.toString().length()) {
				setCustomMessage(constraintValidatorContext, "links must be a valid JSON");
				return false;
			}

			JsonNode titleNode = rootNode.get("title");

			if (titleNode != null && titleNode.asText().length() > 20) {
				setCustomMessage(constraintValidatorContext, "title must not exceed 20 characters");
				return false;
			}

			return true;
		} catch (JsonProcessingException e) {
			setCustomMessage(constraintValidatorContext, "links must be a valid JSON");
			return false;
		}
	}

	private void setCustomMessage(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
	}
}
