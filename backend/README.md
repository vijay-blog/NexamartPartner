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

## One-time Flyway checksum repair

The known legacy V1 checksum is repaired automatically once. For any other
intentional checksum repair, set `FLYWAY_REPAIR_ON_MIGRATE=true` for one
deployment, then remove it so subsequent deployments validate migrations
strictly.
