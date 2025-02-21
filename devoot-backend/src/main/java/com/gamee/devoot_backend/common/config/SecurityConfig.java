package com.gamee.devoot_backend.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gamee.devoot_backend.user.firebase.FirebaseAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final FirebaseAuthenticationFilter firebaseAuthenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/error").permitAll()
				.requestMatchers("/api/users/check-profile-id").permitAll()
				.requestMatchers("/api/users/register").permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				.requestMatchers("/api/users/me").authenticated() // 인증 필요
				.requestMatchers("/api/lectures/search").permitAll()
				.requestMatchers("/api/login").permitAll()
				.requestMatchers(request -> request.getRequestURI().matches("/api/lectures/\\d+")).permitAll()
				.requestMatchers(request -> request.getRequestURI().matches("/api/reviews/lectures/\\d+")).permitAll()
				.requestMatchers(request -> request.getRequestURI().matches("/api/users.register")).permitAll()
				.anyRequest().authenticated()
			)
			.addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://i12a209.p.ssafy.io", "http://devoot-frontend", "http://devoot-admin:8100")); // 허용할 클라이언트 도메인
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
		configuration.setAllowedHeaders(List.of("*")); // 허용할 헤더
		configuration.setAllowCredentials(true); // 쿠키 허용 여부
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // 모든 경로에" 대해 적용
		return source;
	}
}
