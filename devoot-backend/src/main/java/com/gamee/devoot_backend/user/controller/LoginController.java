package com.gamee.devoot_backend.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gamee.devoot_backend.user.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/login")
@Validated
public class LoginController {
	/**
	 * 현재 인증된 사용자의 정보 조회하는 메서드.
	 *
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @return 현재 인증된 사용자의 정보(CustomUserDetails).
	 * 		성공 시 상태코드 200 OK 반환.
	 */
	@GetMapping
	public ResponseEntity<CustomUserDetails> getLoginInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ResponseEntity.ok(userDetails);
	}
}
