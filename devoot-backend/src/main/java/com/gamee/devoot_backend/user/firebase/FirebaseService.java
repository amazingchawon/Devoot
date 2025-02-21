package com.gamee.devoot_backend.user.firebase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gamee.devoot_backend.common.exception.DevootException;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.exception.UserInvalidTokenException;
import com.gamee.devoot_backend.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirebaseService {
	private final UserRepository userRepository;

	private final FirebaseAuth firebaseAuth;

	public Optional<User> findUserByUid(String uid) {
		return userRepository.findByUid(uid);
	}

	public DecodedToken parseToken(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new UserInvalidTokenException();
		}

		String token = authorizationHeader.substring(7);
		try {
			FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
			String uid = firebaseToken.getUid();
			String email = (String)firebaseToken.getClaims().get("email");
			return new DecodedToken(uid, email);
		} catch (FirebaseAuthException e) {
			throw new DevootException(
				UserErrorCode.fromFirebaseErrorCode(e.getAuthErrorCode() != null ? e.getAuthErrorCode().name() : null),
				e.getMessage()
			);
		}
	}

	public record DecodedToken(String uid, String email) {
	}
}
