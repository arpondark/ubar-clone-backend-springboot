# Uber Clone Backend Microservices

Spring Boot microservices backend for a ride-hailing application. This project demonstrates event-driven architecture using Apache Kafka, caching with Redis, and MySQL for persistent storage.

## Quick Start (30 Seconds)

Open PowerShell and run these commands in separate terminal windows:

Terminal 1 - Start Infrastructure:
```powershell
docker-compose up -d
```

Terminal 2 - Location Service:
```powershell
cd location-service
.\mvnw.cmd spring-boot:run
```

Terminal 3 - Ride Service:
```powershell
cd ride-service
.\mvnw.cmd spring-boot:run
```

Terminal 4 - Matching Service:
```powershell
cd maching-service
.\mvnw.cmd spring-boot:run
```

Wait 30-60 seconds for services to initialize. Verify with:
```powershell
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

All should return: {"status":"UP"}

## Project Architecture

The system consists of three main microservices:

1. Location Service (Port 8082) - Manages driver locations in real-time using Redis
2. Ride Service (Port 8083) - Handles ride requests and lifecycle management with MySQL
3. Matching Service (Port 8084) - Performs ride-to-driver matching using Kafka events

These services communicate via:
- Kafka for asynchronous event processing (ride-requested, ride-mached topics)
- OpenFeign for synchronous HTTP calls between services

## Prerequisites and Dependencies

System Requirements:
- JDK 21 or higher (LTS recommended)
- Maven 3.8.1+ (included as mvnw.cmd)
- Docker and Docker Compose
- Git (for version control)

External Services (started via docker-compose):
- MySQL Database: localhost:3306
- Redis Cache: localhost:6379
- Apache Kafka: localhost:9092
- ZooKeeper: localhost:2181

Optional:
- Postman: For API testing
- IntelliJ IDEA: For IDE development

## Complete Installation and Setup Guide

## Step 1: Clone the Repository

```powershell
git clone https://github.com/arpondark/ubar-clone-backend-springboot.git
cd ubar-clone-backend-springboot
```

## Step 2: Verify Prerequisites

Check Java version:
```powershell
java -version
```

Expected: Java 21 or higher

Check Docker:
```powershell
docker --version
docker-compose --version
```

## Step 3: Start Infrastructure Services

Start all required Docker containers:

```powershell
docker-compose up -d
```

Verify services are running:

```powershell
docker-compose ps
```

Expected output shows: MySQL, Redis, ZooKeeper, Kafka running

## Step 4: Clear IDE Cache (If Using IntelliJ IDEA)

If recently updated pom.xml:

1. Close IntelliJ IDEA completely
2. File > Invalidate Caches > Invalidate and Restart

This ensures Maven loads correct dependencies.

## Step 5: Run the Services

Each service must run in a separate terminal window.

### Location Service (Port 8082)

```powershell
cd location-service
.\mvnw.cmd spring-boot:run
```

Expected: Service starts on http://localhost:8082

### Ride Service (Port 8083)

```powershell
cd ride-service
.\mvnw.cmd spring-boot:run
```

Expected: Service starts on http://localhost:8083 with MySQL connected

### Matching Service (Port 8084)

```powershell
cd maching-service
.\mvnw.cmd spring-boot:run
```

Expected: Service starts on http://localhost:8084 with Kafka listening

## Step 6: Verify All Services Running

In a new terminal:

```powershell
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

All should return: {"status":"UP"}


## Complete API Reference

### Location Service (Port 8082)

Update Driver Location:
```
POST /api/v1/locations/drivers/update
Content-Type: application/json

{
  "driverId": "driver-001",
  "lattitude": 40.7128,
  "longitude": -74.0060
}
```

Get Nearby Drivers:
```
GET /api/v1/locations/drivers/nearby?lattitude=40.7128&longitude=-74.0060&radius=5.0
```

Response:
```json
[
  {
    "driverId": "driver-001",
    "lattitude": 40.7128,
    "longitude": -74.0060,
    "distanceInKm": 0.5
  }
]
```

Remove Driver:
```
DELETE /api/v1/locations/drivers/{driverId}
```

### Ride Service (Port 8083)

Request a Ride (This triggers Kafka event and Matching Service):
```
POST /api/v1/rides/request
Content-Type: application/json

{
  "riderId": "rider-001",
  "pickupLatitude": 40.7128,
  "pickupLongitude": -74.0060,
  "pickupAddress": "123 Main St, New York, NY",
  "dropoffLatitude": 40.7580,
  "dropoffLongitude": -73.9855,
  "dropoffAddress": "Empire State Building, New York, NY"
}
```

Get Ride by ID:
```
GET /api/v1/rides/{rideId}
```

Get All Rides for Rider:
```
GET /api/v1/rides/rider/{riderId}
```

Start Ride:
```
PUT /api/v1/rides/{rideId}/start
```

### Matching Service (Port 8084)

Health Check:
```
GET /actuator/health
```

Service Info:
```
GET /actuator/info
```

## Testing with Postman or PowerShell

Using Postman:
1. Open Postman
2. Click Import
3. Select Uber-Clone-API-Collection.postman_collection.json
4. Execute requests from organized collection

Using PowerShell:

Update driver location:
```powershell
$body = @{driverId="driver-001"; lattitude=40.7128; longitude=-74.0060} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8082/api/v1/locations/drivers/update" -Method POST -ContentType "application/json" -Body $body
```

Get nearby drivers:
```powershell
Invoke-WebRequest -Uri "http://localhost:8082/api/v1/locations/drivers/nearby?lattitude=40.7128&longitude=-74.0060&radius=5.0" -Method GET
```

Request a ride (triggers Matching Service):
```powershell
$body = @{
    riderId="rider-001"
    pickupLatitude=40.7128
    pickupLongitude=-74.0060
    pickupAddress="123 Main St"
    dropoffLatitude=40.7580
    dropoffLongitude=-73.9855
    dropoffAddress="Empire State Building"
} | ConvertTo-Json
Invoke-WebRequest -Uri "http://localhost:8083/api/v1/rides/request" -Method POST -ContentType "application/json" -Body $body
```

## Service Configuration Files

### Location Service (Port 8082)

File: location-service/src/main/resources/application.yaml

- Redis: localhost:6379 (for geospatial indexes)
- Features: Driver location tracking and queries
- Storage: Redis cache only (no persistent database)

### Ride Service (Port 8083)

File: ride-service/src/main/resources/application.yaml

- MySQL: localhost:3306/ride_service
- Redis: localhost:6379 (for caching)
- Kafka: localhost:9092 (event publishing)
- Database user: root
- Database password: arpon007

### Matching Service (Port 8084)

File: maching-service/src/main/resources/application.yaml

- Redis: localhost:6379
- Kafka: localhost:9092
- Location Service URL: http://localhost:8082
- Spring Boot: 3.4.13
- Spring Cloud: 2024.0.1

## Kafka Event Flow

When ride is requested:

1. Rider calls POST /api/v1/rides/request on Ride Service
2. Ride Service publishes RideRequestedEvent to Kafka topic: ride-requested
3. Matching Service KafkaListener receives event (group: maching-service-group)
4. Matching Service calls Location Service to find nearby drivers
5. Matching Service selects best driver using scoring algorithm:
   - Distance Score: 1.0 / (distance + 0.1) with 70% weight
   - Rating Score: simulated 4.0-5.0 with 30% weight
6. Matching Service publishes RideMachedEvent to Kafka topic: ride-mached
7. Ride Service consumes ride-mached event and updates ride status

Topics used:
- ride-requested: Driver matching requests
- ride-mached: Matched driver notifications

## Technology Stack

Core:
- Spring Boot 3.4.13 (JVM Framework)
- Spring Cloud 2024.0.1 (Microservices)
- Spring Data Redis (Caching)
- Spring Kafka (Event Streaming)
- Spring Cloud OpenFeign (HTTP Client)

Data:
- MySQL 8 (Persistent storage for rides)
- Redis 7+ (Location cache and geospatial indexes)

Messaging:
- Apache Kafka (Event broker)
- ZooKeeper (Kafka coordination)

Development:
- Java 21 LTS
- Maven 3.8.1+
- Lombok (Code generation)
- JUnit 5 (Testing)

## Troubleshooting and Common Issues

Issue: ClassNotFoundException: SpringDataWebProperties
Cause: Spring Boot and Spring Cloud version mismatch
Solution: Spring Boot 3.4.13 with Spring Cloud 2024.0.1 is correct and tested

Issue: Feign client fails to initialize
Cause: Location Service not running or URL incorrect
Solution: Start Location Service first, verify http://localhost:8082 is accessible

Issue: Kafka consumer fails to start
Cause: Kafka broker not running
Solution: Verify with docker-compose ps, restart with docker-compose up -d

Issue: Redis connection refused
Cause: Redis container not running
Solution: Check docker-compose ps, ensure Redis is running on port 6379

Issue: MySQL connection failed
Cause: MySQL not started or credentials wrong
Solution: Verify credentials in ride-service application.yaml, check port 3306

Issue: Port already in use (8082, 8083, or 8084)
Solution: Find process using port: netstat -ano | findstr :8082
Kill process: taskkill /PID <PID> /F

Issue: IntelliJ IDEA showing old dependencies
Solution: File > Invalidate Caches > Invalidate and Restart

Issue: Services not communicating
Cause: Firewall or DNS resolution
Solution: Verify with curl, check Docker bridge network, restart docker-compose

## Building and Compilation

Build without running:
```powershell
cd maching-service
.\mvnw.cmd clean install -DskipTests
```

Run tests:
```powershell
.\mvnw.cmd test
```

Build packages as JAR:
```powershell
.\mvnw.cmd clean package
java -jar target/maching-service-0.0.1-SNAPSHOT.jar
```

## Stopping and Cleanup

Stop all Docker services:
```powershell
docker-compose down
```

Stop Docker and remove volumes (clears data):
```powershell
docker-compose down -v
```

Stop service instances:
Press CTRL+C in each service terminal window

Clear Maven cache:
```powershell
cd maching-service
.\mvnw.cmd clean
```

## IDE Setup (IntelliJ IDEA)

1. Open project in IntelliJ
2. Wait for Maven to download dependencies
3. Right-click maching-service > pom.xml > Maven > Reload Project
4. File > Invalidate Caches > Invalidate and Restart
5. Run each service: Right-click Application.java > Run

## Performance Monitoring

Health Checks:
```
Location Service: GET http://localhost:8082/actuator/health
Ride Service: GET http://localhost:8083/actuator/health
Matching Service: GET http://localhost:8084/actuator/health
```

Monitor Docker resources:
```powershell
docker stats
```

View service logs:
```powershell
docker-compose logs -f
```

Check Kafka topics:
```powershell
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Project Structure

```
ubar-clone-backend-springbot/
├── location-service/              Location tracking (Port 8082)
│   ├── src/main/java/             Source code
│   ├── pom.xml                    Maven dependencies
│   └── src/main/resources/        Configuration
├── ride-service/                  Ride management (Port 8083)
│   ├── src/main/java/             Source code
│   ├── pom.xml                    Maven dependencies
│   └── src/main/resources/        Configuration
├── maching-service/               Driver matching (Port 8084)
│   ├── src/main/java/             Source code
│   ├── pom.xml                    Maven dependencies
│   └── src/main/resources/        Configuration
├── docker-compose.yml             Infrastructure (MySQL, Redis, Kafka)
├── README.md                      This file
└── Uber-Clone-API-Collection.postman_collection.json
```

## Version Information

Current Versions (Fixed and Tested):
- Spring Boot: 3.4.13 (LTS compatible)
- Spring Cloud: 2024.0.1
- Java: 21 LTS (recommended) or 25 (latest)
- Maven: 3.8.1+

Previous Issue (Resolved):
- Spring Boot 4.0.6 caused ClassNotFoundException
- Fixed by downgrading to 3.4.13 with compatible Spring Cloud

## Next Steps

1. Import Postman collection and test all endpoints
2. Monitor logs in each service terminal
3. Verify Kafka event flow between services
4. Check MySQL database for ride data
5. Customize matching algorithm as needed in maching-service

