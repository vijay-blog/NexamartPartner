package com.nexamart.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
  private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

  @Bean
  FlywayMigrationStrategy flywayMigrationStrategy(
      @Value("${nexamart.flyway-repair-on-migrate:false}") boolean repairOnMigrate) {
    return flyway -> {
      if (repairOnMigrate) {
        logger.warn("Repairing Flyway schema history before migration");
        flyway.repair();
      }
      flyway.migrate();
    };
  }
}
