# Ubar Clone Backend

Spring Boot microservices backend for a ride-hailing clone.

## Services

### Location Service

Tracks and queries driver locations using Redis geospatial indexes.

- Docs: [`location-service/README.md`](location-service/README.md)
- Port: `8082`
- Storage: Redis

### Ride Service

Handles ride requests, ride lifecycle updates, and Kafka event publishing.

- Docs: [`ride-service/README.md`](ride-service/README.md)
- Port: `8083`
- Storage: MySQL
- Messaging: Kafka

### Matching Service

The repository also includes `maching-service/`, which appears to be reserved for rider/driver matching logic.

## Local Infrastructure

The root `docker-compose.yml` starts the shared dependencies used by the services:

- Redis on `6379`
- ZooKeeper on `2181`
- Kafka on `9092`

## Quick Start

1. Start the shared services:

```bash
docker compose up -d
```

2. Run the Spring Boot services from their module directories.

### Windows PowerShell

```powershell
cd .\location-service; .\mvnw.cmd spring-boot:run
```

```powershell
cd .\ride-service; .\mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
cd location-service && ./mvnw spring-boot:run
```

```bash
cd ride-service && ./mvnw spring-boot:run
```

## API Summary

### Location Service

- `POST /api/v1/locations/drivers/update`
- `GET /api/v1/locations/drivers/nearby`
- `DELETE /api/v1/locations/drivers/{driverId}`

### Ride Service

- `POST /api/v1/rides/request`
- `GET /api/v1/rides/{rideId}`
- `GET /api/v1/rides/rider/{riderId}`
- `PUT /api/v1/rides/{riderId}/start`

## Notes

- Location data is stored in Redis, not in a relational database.
- Ride data is persisted in MySQL.
- Ride events are published to Kafka for downstream processing.
- Check the service READMEs for full request/response examples and implementation details.

