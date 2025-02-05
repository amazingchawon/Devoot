package com.gamee.devoot_backend.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
		// Check if FirebaseApp is already initialized
		List<FirebaseApp> apps = FirebaseApp.getApps();
		if (apps.isEmpty()) {
			// 로컬 빌드 환경에서는 "local", Jenkins 빌드 환경에서는 "jenkins" 문자열이 제공된다.
			String env = System.getProperty("env", "local");
			// If not, initialize FirebaseApp
			InputStream serviceAccount;
			if (env.equalsIgnoreCase("jenkins")) {
				serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-adminsdk.json");
				if (serviceAccount == null) {
					throw new FileNotFoundException("firebase-config.json not found in classpath");
				}
			} else {
				serviceAccount = new FileInputStream(firebaseConfigPath);
			}
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

			return FirebaseApp.initializeApp(options);
		}
		// Return the existing FirebaseApp instance
		return FirebaseApp.getInstance();
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
