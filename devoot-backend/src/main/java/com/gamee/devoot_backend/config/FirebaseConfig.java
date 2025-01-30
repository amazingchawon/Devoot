package com.gamee.devoot_backend.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
public class FirebaseConfig {
	@Value("${firebase.config.path}")
	private String firebaseConfigPath;

	@Bean
	public FirebaseApp firebaseApp() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();

		return FirebaseApp.initializeApp(options);
	}

	@Bean
	public FirebaseAuth getFirebaseAuth() {
		try {
			return FirebaseAuth.getInstance(firebaseApp());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
