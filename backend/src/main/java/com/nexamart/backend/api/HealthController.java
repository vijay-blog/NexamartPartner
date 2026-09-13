package com.nexamart.backend.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
  private final String commit;

  HealthController(@Value("${nexamart.build-commit:unknown}") String commit) {
    this.commit = commit;
  }

  @GetMapping("/health")
  Map<String, Object> health() {
    return Map.of(
      "status", "UP",
      "service", "nexamart-partner-backend",
      "buildCommit", commit,
      "registrationPublic", true
    );
  }
}
