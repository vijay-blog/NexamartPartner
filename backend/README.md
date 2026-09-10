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

If a deployed migration checksum no longer matches its schema-history entry, set
`FLYWAY_REPAIR_ON_MIGRATE=true` for one deployment. After the application starts
successfully, remove the variable so subsequent deployments validate migrations
strictly.
