package com.nexamart.backend.security;

import com.nexamart.backend.config.AppProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

  @Test
  void rejectsMissingSecret() {
    AppProperties props = new AppProperties();
    JwtService service = new JwtService(props);
    assertThrows(IllegalStateException.class, service::validateConfiguration);
  }

  @Test
  void rejectsShortSecret() {
    AppProperties props = new AppProperties();
    props.setJwtSecret("too-short");
    JwtService service = new JwtService(props);
    assertThrows(IllegalStateException.class, service::validateConfiguration);
  }

  @Test
  void acceptsStrongSecret() {
    AppProperties props = new AppProperties();
    props.setJwtSecret("01234567890123456789012345678901");
    JwtService service = new JwtService(props);
    assertDoesNotThrow(service::validateConfiguration);
  }

  @Test
  void createsAndValidatesAccessAndRefreshTokens() {
    AppProperties props = new AppProperties();
    props.setJwtSecret("01234567890123456789012345678901");
    JwtService service = new JwtService(props);
    service.validateConfiguration();

    String access = service.accessToken(42L, "admin", "ADMIN");
    String refresh = service.refreshToken(42L, "admin", "ADMIN");

    assertDoesNotThrow(() -> service.parse(access));
    assertDoesNotThrow(() -> service.parse(refresh));
    org.junit.jupiter.api.Assertions.assertTrue(service.validAccess(access));
    org.junit.jupiter.api.Assertions.assertTrue(service.validRefresh(refresh));
  }
}
