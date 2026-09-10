package com.nexamart.backend.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
  private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);
  private static final String HISTORY_TABLE = "nexamart_partner_flyway_history";

  @Bean
  FlywayMigrationStrategy flywayMigrationStrategy() {
    return flyway -> {
      if (hasFailedV9(flyway.getConfiguration().getDataSource())) {
        logger.warn("Repairing failed partner migration V9 before retry");
        flyway.repair();
      }
      flyway.migrate();
    };
  }

  private boolean hasFailedV9(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metadata = connection.getMetaData();
      try (ResultSet tables =
          metadata.getTables(connection.getCatalog(), null, HISTORY_TABLE, new String[] {"TABLE"})) {
        if (!tables.next()) {
          return false;
        }
      }
      try (var statement =
          connection.prepareStatement(
              "SELECT success FROM " + HISTORY_TABLE + " WHERE version = ?")) {
        statement.setString(1, "9");
        try (ResultSet result = statement.executeQuery()) {
          return result.next() && !result.getBoolean("success");
        }
      }
    } catch (SQLException exception) {
      throw new IllegalStateException("Unable to inspect partner Flyway history", exception);
    }
  }
}
