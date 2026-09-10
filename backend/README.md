# NexaMart Partner Backend

Spring Boot 3.3 / Java 17 service for Railway.

## Railway service settings
- Root Directory: partner-backend
- Build: Dockerfile
- Healthcheck: /api/v1/health
- Port: Railway PORT environment variable

## Required variables
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- JWT_SECRET (32+ random characters)

partner service uses the shared NexaMart MySQL schema so customer-created orders are immediately visible to the partner service.

## Flyway history

This service records migrations in `nexamart_partner_flyway_history`, separate
from other services that share the database. Existing shared schemas are
baselined at version 5; empty databases still run the initial schema migration.
