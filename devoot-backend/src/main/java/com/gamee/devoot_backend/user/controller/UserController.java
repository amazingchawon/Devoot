package com.gamee.devoot_backend.user.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gamee.devoot_backend.common.pageutils.CustomPage;
import com.gamee.devoot_backend.follow.dto.FollowUserDto;
import com.gamee.devoot_backend.follow.service.FollowService;
import com.gamee.devoot_backend.lecturereview.dto.LectureReviewDto;
import com.gamee.devoot_backend.lecturereview.service.LectureReviewService;
import com.gamee.devoot_backend.user.dto.AdminDetailDto;
import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.dto.UserDetailDto;
import com.gamee.devoot_backend.user.dto.UserRegistrationDto;
import com.gamee.devoot_backend.user.dto.UserShortDetailDto;
import com.gamee.devoot_backend.user.dto.UserUpdateDto;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.firebase.FirebaseService;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
	private final FirebaseService firebaseService;
	private final UserService userService;
	private final LectureReviewService lectureReviewService;
	private final FollowService followService;

	@GetMapping("/reported")
	public ResponseEntity<?> findReportedUsers(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "1") @Positive int size
	) {
		return ResponseEntity.ok(userService.findReportedUsers(userDetails, page, size));
	}

	/**
	 * 회원가입 시 profile ID 중복 체크 메서드.
	 *
	 * @param profileId
	 * 		중복 확인하고자하는 profile ID.
	 * @return
	 *      true: profile ID가 사용 가능 (중복되지 않음).
	 *   	false: profile ID가 이미 사용 중 (중복됨).
	 */
	@GetMapping("/check-profile-id")
	public ResponseEntity<Boolean> checkProfileId(
		@RequestParam String profileId
	) {
		boolean isAvailable = !userService.existsByProfileId(profileId, null);
		return ResponseEntity.ok(isAvailable);
	}

	/**
	 * 로그인 시 profile ID 중복 체크 메서드.
	 *
	 * @param profileId
	 * 		중복 확인하고자하는 profile ID.
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @return
	 *      true: profile ID 사용 가능 (본인의 ID or 중복되지 않음).
	 * 		false: profile ID가 이미 다른 사용자에 의해 사용 중 (중복됨).
	 */
	@GetMapping("/check-profile-id/authenticated")
	public ResponseEntity<Boolean> checkProfileIdAuthenticated(
		@RequestParam String profileId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		boolean isAvailable = !userService.existsByProfileId(profileId, userDetails);
		return ResponseEntity.ok(isAvailable);
	}

	/**
	 * 사용자 검색 메서드
	 *
	 * @param query
	 *		  사용자가 입력한 검색 쿼리 string
	 * @param userDetails
	 * 		  현재 인증된 사용자 정보를 나타내는 객체.
	 * @return List<UserShortDetailDto> 검색 결과에 뜨는 사용자 정보만 담은 사용자 객체 리스트
	 */
	@GetMapping
	public ResponseEntity<CustomPage<UserShortDetailDto>> searchUsers(
		@RequestParam(name = "q") String query,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "1") @Positive int size,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		CustomPage<UserShortDetailDto> users = userService.searchByPrefix(query, page, size);
		return ResponseEntity.ok(users);
	}

	/**
	 * 사용자 회원가입 메서드.
	 *
	 * @param authorizationHeader
	 * 		Firebase 토큰 포함, 해당 토큰을 검증하여 사용자 인증.
	 * @param userRegistrationDto
	 * 		등록하려는 사용자의 상세 정보.
	 * @return 생성된 사용자 정보(CustomUserDetails)를 포함한 HTTP 응답.
	 * 		성공 시 상태코드 201 Created.
	 */
	@PostMapping(value = "/register", consumes = {"multipart/form-data"})
	public ResponseEntity<?> registerUser(
		@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
		@RequestPart("user") @Valid UserRegistrationDto userRegistrationDto,
		@RequestPart(value = "file", required = false) MultipartFile file) {
		var decoded = firebaseService.parseToken(authorizationHeader);

		User newUser = userService.registerUser(decoded.uid(), userRegistrationDto, file);
		CustomUserDetails userDetails = new CustomUserDetails(newUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(userDetails);
	}

	/**
	 * 현재 인증된 사용자의 정보 조회하는 메서드.
	 *
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @return 현재 인증된 사용자의 프로필 기본 정보.
	 * 		성공 시 상태코드 200 OK 반환.
	 */
	@GetMapping("/{profileId}")
	public ResponseEntity<UserDetailDto> getMyInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@PathVariable String profileId) {
		UserDetailDto dto = userService.getUserInfo(userDetails, profileId);
		return ResponseEntity.ok(dto);
	}

	/**
	 * 현재 인증된 사용자의 상세 정보 수정하는 메서드.
	 *
	 * @param userDetails
	 * 		현재 인증된 사용자 정보를 나타내는 객체.
	 * @param userUpdateDto
	 * 		수정할 사용자 정보.
	 * @param file
	 * 		프로필 이미지(옵션).
	 * @return 수정된 사용자 정보(CustomUserDetails)
	 */
	@PatchMapping(value = "/me", consumes = {"multipart/form-data"})
	public ResponseEntity<CustomUserDetails> updateMyInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestPart("user") @Valid UserUpdateDto userUpdateDto,
		@RequestPart(value = "file", required = false) MultipartFile file
	) {
		User updatedUser = userService.updateUser(userDetails.id(), userUpdateDto, file);
		return ResponseEntity.ok(new CustomUserDetails(updatedUser));
	}

	/**
	 * 단일 유저가 작성한 리뷰 목록을 반환
	 * @param profileId
	 * - 리뷰를 작성한 사용자의 profileId
	 * @param page
	 * - 리뷰를 표시할 page 정보
	 * @return
	 * - 리뷰와 페이지 정보가 담긴 Page 객체
	 */
	@GetMapping("/{profileId}/reviews")
	public ResponseEntity<CustomPage<LectureReviewDto>> getReviewListByProfileIdId(@PathVariable(value = "profileId") String profileId,
		@RequestParam(value = "page", defaultValue = "1") int page,
		@AuthenticationPrincipal CustomUserDetails user) {
		Page<LectureReviewDto> lectureReviewDtoPage = lectureReviewService.getLectureReviewByProfileId(profileId, page, user.id());
		return ResponseEntity.status(HttpStatus.OK).body(new CustomPage<>(lectureReviewDtoPage));
	}

	/**
	 * 사용자 A가 팔로우한 사용자 리스트 불러오는 메서드
	 * @param profileId
	 * 		사용자 A의 프로필 ID.
	 * @param page
	 *		페이지네이션 페이지.
	 * @param size
	 * 		페이지네이션 한 페이지 당 가져올 개수.
	 * @return ResponseEntity - 사용자 A가 팔로우한 사용자 리스트 페이지네이션 정보.
	 */
	@GetMapping("/{profileId}/following")
	public ResponseEntity<CustomPage<FollowUserDto>> getFollowing(
		@PathVariable String profileId,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "20") @Positive int size) {
		CustomPage<FollowUserDto> followingPage = followService.getFollowingUsers(profileId, page, size);
		return ResponseEntity.ok(followingPage);
	}

	/**
	 * 사용자 A를 팔로우한 사용자 리스트 불러오는 메서드
	 * @param profileId
	 * 		사용자 A의 프로필 ID.
	 * @param page
	 *		페이지네이션 페이지.
	 * @param size
	 * 		페이지네이션 한 페이지 당 가져올 개수.
	 * @return ResponseEntity - 사용자 A를 팔로우한 사용자 리스트 페이지네이션 정보.
	 */
	@GetMapping("/{profileId}/followers")
	public ResponseEntity<CustomPage<FollowUserDto>> getFollowers(
		@PathVariable String profileId,
		@RequestParam(defaultValue = "1") @Positive int page,
		@RequestParam(defaultValue = "20") @Positive int size) {
		CustomPage<FollowUserDto> followerPage = followService.getFollowers(profileId, page, size);
		return ResponseEntity.ok(followerPage);
	}

	@GetMapping("/admin")
	public ResponseEntity<?> getAdminUsers(
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		List<AdminDetailDto> admins = userService.getAdminUserList(userDetails);
		return ResponseEntity.ok(admins);
	}
}
