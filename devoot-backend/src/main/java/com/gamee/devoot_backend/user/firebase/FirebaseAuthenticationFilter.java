package com.gamee.devoot_backend.user.firebase;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gamee.devoot_backend.user.dto.CustomUserDetails;
import com.gamee.devoot_backend.user.entity.User;
import com.gamee.devoot_backend.user.exception.UserErrorCode;
import com.gamee.devoot_backend.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {
	private final UserService userService;
	private final FirebaseService firebaseService;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		String method = request.getMethod();
		String authorizationHeader = request.getHeader("Authorization");

		if ("/api/users/register".equals(requestUri)
			|| "/api/users/check-profile-id".equals(requestUri)) {
			if (!isValidFirebaseToken(authorizationHeader)) {
				writeJsonError(response, UserErrorCode.USER_INVALID_TOKEN, "No or invalid token for register");
				return;
			}
			Logger logger = Logger.getLogger("filterlog");
			logger.log(Level.INFO, "filter entered.");
			try {
				filterChain.doFilter(request, response);
			} catch (Exception e) {
				logger.log(Level.INFO, "error occured.");
				logger.log(Level.INFO, e.toString());
			}
			return;
		}
		// Token이 없어도 접근 가능한 API
		if (authorizationHeader == null && "GET".equalsIgnoreCase(method)
			&& (requestUri.matches("^/api/lectures/\\d+") || requestUri.matches("^/api/lectures/search/*") || requestUri.matches("^/api/reviews/lectures/\\d+"))) {
			filterChain.doFilter(request, response);
			return;
		}
		// Token이 없어도 접근 가능한 API
		if (authorizationHeader == null && "GET".equalsIgnoreCase(method)
			&& (requestUri.matches("^/api/lectures/\\d+") || requestUri.matches("^/api/lectures/search/*") || requestUri.matches("^/api/reviews/lectures/\\d+"))) {
			filterChain.doFilter(request, response);
			return;
		}
		// Token이 없어도 접근 가능한 API
		if (authorizationHeader == null && "GET".equalsIgnoreCase(method)
			&& (requestUri.matches("^/api/lectures/\\d+") || requestUri.matches("^/api/lectures/search/*") || requestUri.matches("^/api/reviews/lectures/\\d+"))) {
			filterChain.doFilter(request, response);
			return;
		}
		// Token이 없어도 접근 가능한 API
		if (authorizationHeader == null && "GET".equalsIgnoreCase(method)
			&& (requestUri.matches("^/api/lectures/\\d+") || requestUri.matches("^/api/lectures/search/*") || requestUri.matches("^/api/reviews/lectures/\\d+"))) {
			filterChain.doFilter(request, response);
			return;
		}

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				FirebaseService.DecodedToken decodedToken = firebaseService.parseToken(authorizationHeader);
				Optional<User> userOpt = firebaseService.findUserByUid(decodedToken.uid());
				if (userOpt.isPresent()) {
					User user = userOpt.get();
					CustomUserDetails userDetails = new CustomUserDetails(user);
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				} else {
					writeJsonError(response, UserErrorCode.USER_NOT_FOUND);
					return;
				}
			} catch (Exception e) {
				writeJsonError(response, UserErrorCode.USER_INVALID_TOKEN);
				return;
			}
		} else {
			writeJsonError(response, UserErrorCode.USER_INVALID_TOKEN, "Missing Bearer token");
			return;
		}
		filterChain.doFilter(request, response);
	}

	private boolean isValidFirebaseToken(String header) {
		if (header == null || !header.startsWith("Bearer ")) {
			return false;
		}
		try {
			firebaseService.parseToken(header); // 내부에서 FirebaseAuth.verifyIdToken
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void writeJsonError(HttpServletResponse response, UserErrorCode errorCode) throws IOException {
		writeJsonError(response, errorCode, null);
	}

	private void writeJsonError(HttpServletResponse response, UserErrorCode errorCode, String detail) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json;charset=UTF-8");
		String jsonBody =
			"""
				{
					"status": %d,
					"code": "%s",
					"message": "%s"
					%s
				}
				""".formatted(
				errorCode.getStatus().value(),
				errorCode.getCode(),
				errorCode.getMessage(),
				detail != null ? ", \"detail\": \"" + escapeJson(detail) + "\"" : ""
			);

		response.getWriter().write(jsonBody);
		response.getWriter().flush();
	}

	private String escapeJson(String text) {
		return text.replace("\"", "\\\"").replace("\n", "");
	}
}
