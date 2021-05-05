package com.example.security.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class JwtService {

	private static final String EXPIRY_TIME = "expiryTime";
	private static final String ISSUED_AT = "issuedAt";
	private static final String ID = "id";
	private static final String EMAIL = "email";
	private static final String SECRET_KEY ="avjovHEadkaoscqiehqor";

	public Algorithm getAuthAlgorithm() {
		return Algorithm.HMAC256(SECRET_KEY);
	}

	public JwtUser decode(String jwtToken) {
		try {
			DecodedJWT s = JWT.decode(jwtToken);
			JWTVerifier verifier = JWT.require(getAuthAlgorithm()).build();

			verifier.verify(s);

			if (LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) > s.getClaim(EXPIRY_TIME).asLong()) {
				throw new TokenExpiredException("expired token");
			}

			return JwtUser.of(s.getClaim(ID).asString(),
				s.getClaim(EMAIL).asString(),
				LocalDateTime.ofEpochSecond(s.getClaim(ISSUED_AT).asLong(), 0, ZoneOffset.UTC),
				LocalDateTime.ofEpochSecond(s.getClaim(EXPIRY_TIME).asLong(), 0, ZoneOffset.UTC));
		} catch (JWTDecodeException e) {
			throw new BadCredentialsException("Invalid access token");
		}
	}

	public String encode(JwtUser user) {
		return JWT.create().withJWTId(user.jti)
			.withClaim(ID, user.userId)
			.withClaim(EMAIL,user.email)
			.withClaim(ISSUED_AT, user.issuedAt.toEpochSecond(ZoneOffset.UTC))
			.withClaim(EXPIRY_TIME, user.expiryTime.toEpochSecond(ZoneOffset.UTC))
			.sign(getAuthAlgorithm());
	}

	@Getter
	@AllArgsConstructor
	public static class JwtUser {
		private final String userId;
		private final String email;
		private final LocalDateTime issuedAt;
		private final LocalDateTime expiryTime;
		private final String jti;

		public static JwtUser of(String userId, String email ,LocalDateTime issuedAt, LocalDateTime expiryAt) {
			return new JwtUser(userId,email, issuedAt, expiryAt, UUID.randomUUID().toString());
		}
	}
}
