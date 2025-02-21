package com.gamee.devoot_backend.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.common.exception.JsonParsingException;

@Component
public class Util {
	public static final ObjectMapper objectMapper = new ObjectMapper();

	public static JsonNode parseToJson(String jsonString) throws JsonParsingException {
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readTree(jsonString);
		} catch (Exception e) {
			throw new JsonParsingException();
		}
	}

	public static String sha256(String message) {
		if (message == null) {
			return null;
		}
		MessageDigest sha;
		String result = "";
		try {
			sha = MessageDigest.getInstance("SHA-256");
			sha.update(message.getBytes());
			for (byte b : sha.digest()) {
				result += Integer.toHexString(b & 0xff);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
