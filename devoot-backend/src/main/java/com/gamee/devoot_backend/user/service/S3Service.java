package com.gamee.devoot_backend.user.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.common.exception.FileNameGenerationFailedException;
import com.gamee.devoot_backend.common.exception.S3OperationFailedException;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Template s3Template;
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${s3.file.salt}")
	private static String fileSalt;

	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

	public String uploadFile(Long userId, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		validateImageFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

		String fileName = "profile/" + generateUserFileName(userId) + ".jpg";
		try (InputStream inputStream = file.getInputStream()) {
			s3Template.upload(
				bucketName,
				fileName,
				inputStream
			);
		} catch (IOException e) {
			throw new S3OperationFailedException();
		}
		return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
	}

	private void validateImageFileExtension(String filename) {
		int lastDotIndex = filename.lastIndexOf('.');
		if (lastDotIndex == -1) {
			throw new S3OperationFailedException("Invalid image file format");
		}

		String extension = filename.substring(lastDotIndex + 1).toLowerCase();
		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new S3OperationFailedException("Invalid image file format");
		}
	}

	private static String generateUserFileName(Long userId) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String textToHash = userId + fileSalt;
			byte[] hash = digest.digest(textToHash.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < 4; i++) {
				hexString.append(Integer.toHexString(0xff & hash[i]));
			}

			return userId + "-" + hexString;
		} catch (NoSuchAlgorithmException e) {
			throw new FileNameGenerationFailedException();
		}
	}
}
