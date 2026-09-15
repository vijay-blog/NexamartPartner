package com.nexamart.backend.security;

import com.nexamart.backend.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Production JWT service.
 *
 * JWT_SECRET is intentionally required to be a strong HMAC secret.  Do not
 * silently hash/transform a weak secret: a bad production secret should make
 * the deployment fail fast instead of allowing login to fail later with a
 * generic HTTP 500.
 */
@Service
public class JwtService {
  private static final int MIN_SECRET_BYTES = 32;

  private final AppProperties props;

  public JwtService(AppProperties props) {
    this.props = props;
  }

  @PostConstruct
  void validateConfiguration() {
    String secret = props.getJwtSecret();
    if (secret == null || secret.isBlank()) {
      throw new IllegalStateException(
          "JWT_SECRET is missing. Set a strong JWT_SECRET (at least 32 UTF-8 bytes) in Railway environment variables.");
    }
    if (secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
      throw new IllegalStateException(
          "JWT_SECRET is too short. Use a random secret of at least 32 UTF-8 bytes in Railway environment variables.");
    }
    if (props.getAccessTokenMinutes() <= 0) {
      throw new IllegalStateException("ACCESS_TOKEN_MINUTES must be greater than 0.");
    }
    if (props.getRefreshTokenDays() <= 0) {
      throw new IllegalStateException("REFRESH_TOKEN_DAYS must be greater than 0.");
    }
  }

  private SecretKey key() {
    // Configuration is validated during startup, so login/refresh cannot
    // unexpectedly fail because JJWT rejects a weak signing key.
    return Keys.hmacShaKeyFor(props.getJwtSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String accessToken(Long id, String identifier, String role) {
    return build(id, identifier, role, props.getAccessTokenMinutes() * 60_000L, "access");
  }

  public String refreshToken(Long id, String identifier, String role) {
    return build(id, identifier, role, props.getRefreshTokenDays() * 86_400_000L, "refresh");
  }

  private String build(Long id, String identifier, String role, long ttl, String type) {
    Instant now = Instant.now();
    return Jwts.builder()
        .claims(Map.of("uid", id, "role", role, "type", type))
        .subject(identifier)
        .issuedAt(Date.from(now))
        .expiration(new Date(now.toEpochMilli() + ttl))
        .signWith(key())
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
  }

  public boolean validAccess(String token) {
    try {
      Claims claims = parse(token);
      return "access".equals(claims.get("type", String.class))
          && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean validRefresh(String token) {
    try {
      Claims claims = parse(token);
      return "refresh".equals(claims.get("type", String.class))
          && claims.getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
