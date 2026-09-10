package com.nexamart.backend.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
  private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);
  private static final int LEGACY_V1_CHECKSUM = -675095963;

  @Bean
  FlywayMigrationStrategy flywayMigrationStrategy(
      @Value("${nexamart.flyway-repair-on-migrate:false}") boolean repairOnMigrate) {
    return flyway -> {
      boolean legacyV1Checksum = hasLegacyV1Checksum(flyway.getConfiguration().getDataSource());
      if (repairOnMigrate || legacyV1Checksum) {
        logger.warn(
            "Repairing Flyway schema history before migration (explicit={}, legacyV1={})",
            repairOnMigrate,
            legacyV1Checksum);
        flyway.repair();
      }
      flyway.migrate();
    };
  }

  private boolean hasLegacyV1Checksum(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metadata = connection.getMetaData();
      try (ResultSet tables =
          metadata.getTables(connection.getCatalog(), null, "flyway_schema_history", new String[] {"TABLE"})) {
        if (!tables.next()) {
          return false;
        }
      }
      try (var statement =
          connection.prepareStatement(
              "SELECT checksum FROM flyway_schema_history WHERE version = ?")) {
        statement.setString(1, "1");
        try (ResultSet result = statement.executeQuery()) {
          return result.next() && result.getInt("checksum") == LEGACY_V1_CHECKSUM;
        }
      }
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to inspect Flyway schema history", exception);
    }
  }
}
