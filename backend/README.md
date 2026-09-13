# NexaMart Partner Backend

Spring Boot 3.3 / Java 17 service for Railway.

## Railway service settings
- Root Directory: repository root
- Config file: /railway.toml
- Build: /Dockerfile (builds only the `backend` Maven module)
- Healthcheck: /api/v1/health
- Port: Railway PORT environment variable

The health response includes Railway's non-secret Git commit SHA. Confirm it
matches the deployed commit before treating a source fix as deployed.

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
