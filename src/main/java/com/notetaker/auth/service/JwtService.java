package com.notetaker.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.notetaker.auth.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	private static final String SECRET_KEY = "BF7FD11ACE545745B7BA1AF98B6F156D127BC7BB544BAB6A4FD74E4FC7";

//	// ------------------ Extract Username ------------------
//	public String extractUsername(String token) {
//		return extractClaim(token, Claims::getSubject);
//	}
//
//	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//		final Claims claims = extractAllClaims(token);
//		return claimsResolver.apply(claims);
//	}
//
//	// ------------------ Extract All Claims ------------------
//	private Claims extractAllClaims(String token) {
//		return Jwts.parserBuilder().setSigningKey(getSignInKey()) // verifyWith() ka replacement
//				.build().parseClaimsJws(token).getBody();
//	}

	// ------------------ Extract Email (Subject) ------------------
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// ------------------ Extract User ID ------------------
	public String extractUserId(String token) {
		return extractClaim(token, claims -> claims.get("userId", String.class));
	}

	// ------------------ Extract Username (Name) ------------------
	public String extractUsername(String token) {
		return extractClaim(token, claims -> claims.get("username", String.class));
	}

	// ------------------ Extract Role ------------------
	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	// ------------------ Generic Claim Extractor ------------------
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// ------------------ Extract All Claims ------------------
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	// ------------------ Generate Token ------------------
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		extraClaims = new HashMap<>(extraClaims);
		User user = (User) userDetails;
		extraClaims.put("userId", user.getId());
		extraClaims.put("username", user.getActualUsername());
		extraClaims.put("email", user.getEmail());
		extraClaims.put("role", userDetails.getAuthorities());

		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 25 * 100000))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	// ------------------ Token Validation ------------------
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractEmail(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// ------------------ SECRET KEY Decode ------------------
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractActualTokenFromBearerAuth(String token) {
		if (token == null || token.isBlank()) {
			throw new RuntimeException("Auth token cannot be null or blank");
		}
		return token.substring(7);
	}
}
