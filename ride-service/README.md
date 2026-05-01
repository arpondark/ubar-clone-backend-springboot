# Ride Service

The Ride Service is the core trip orchestration service for the Ubar clone backend. It handles ride requests, persists ride state in MySQL, and publishes ride-request events to Kafka for matching.

## Responsibilities

- Create a ride request
- Persist ride data and lifecycle status
- Publish ride-request events to Kafka
- Fetch ride details by ride ID
- Fetch rides for a rider
- Update ride status during the trip lifecycle

## Tech Stack

- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Apache Kafka
- Spring Validation
- Lombok

## Runtime Configuration

The service is configured to run on:

- **Port:** `8083`
- **MySQL:** `jdbc:mysql://localhost:3306/ride_service`
- **Redis:** `localhost:6379`
- **Kafka:** `localhost:9092`

### application.yaml

Key settings used by the service:

- `spring.application.name=ride-service`
- `spring.jpa.hibernate.ddl-auto=update`
- `spring.kafka.bootstrap-servers=localhost:9092`

## API Endpoints

Base path: `/api/v1/rides`

### Create Ride Request

`POST /api/v1/rides/request`

Example body:

```json
{
  "riderId": "rider:1",
  "pickupLatitude": 12.9716,
  "pickupLongitude": 77.5946,
  "dropoffLatitude": 12.9352,
  "dropoffLongitude": 77.6245,
  "pickupAddress": "MG Road, Bengaluru",
  "dropoffAddress": "Koramangala, Bengaluru"
}
```

### Get Ride by ID

`GET /api/v1/rides/{rideId}`

### Get Rides for a Rider

`GET /api/v1/rides/rider/{riderId}`

### Start Ride

`PUT /api/v1/rides/{riderId}/start`

> Note: the controller path uses `{riderId}` in the mapping but passes `rideId` to the service. Verify this before exposing it publicly.

### Cancel Ride

The service contains a `cancelRide` operation, but it is not currently mapped in the controller.

### Complete Ride

The service contains a `completedRide` operation, but it is not currently mapped in the controller.

## Ride Flow

1. Rider submits a ride request.
2. Ride Service saves the ride in MySQL with status `REQUESTED`.
3. A Kafka event is published for downstream matching.
4. The ride status moves to `MATCHING`.
5. Once accepted, the ride can move through `ACCEPTED`, `RIDE_STARTED`, and `COMPLETED`.

## Kafka Topics

`KafkaConfig` defines the following topics:

- `ride-requested`
- `ride-matched`

> The current service code publishes to `ride-request`, so align the topic name in code and config before production use.

## Project Structure

- `controller/` – REST endpoints
- `service/` – business logic and ride lifecycle management
- `repo/` – JPA repository layer
- `model/` – ride entity and status enum
- `dtos/` – request and response payloads
- `event/` – Kafka event model
- `config/` – Kafka configuration

## Local Run

1. Start the infrastructure:

```bash
docker compose up -d
```

2. Run the service from the `ride-service` directory.

Using Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## Notes

- The service uses MySQL for ride persistence.
- The service publishes ride requests to Kafka for matching.
- Redis is configured, but it is not actively used in the current ride-service flow.

