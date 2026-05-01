# Location Service

Service for managing and querying driver locations using Redis Geospatial indexes.

## Features

- Update driver coordinates
- Find nearby drivers within a radius
- Remove driver from tracking

## Endpoints

### Update Driver Location
`POST /api/v1/locations/drivers/update`
```json
{
  "driverId": "driver:1",
  "lattitude": 12.9716,
  "longitude": 77.5946
}
```

### Find Nearby Drivers
`GET /api/v1/locations/drivers/nearby?lattitude=12.9716&longitude=77.5946&radius=5.0`

### Remove Driver
`DELETE /api/v1/locations/drivers/{driverId}`

## Seeding Data

Run these commands to seed the Redis cache with sample driver data:

```bash
docker exec -it redis-cache redis-cli GEOADD drivers 77.5946 12.9716 "driver:1"
docker exec -it redis-cache redis-cli GEOADD drivers 77.5800 12.9800 "driver:2"
docker exec -it redis-cache redis-cli GEOADD drivers 77.6100 12.9600 "driver:3"
docker exec -it redis-cache redis-cli GEOADD drivers 77.5500 12.9900 "driver:4"
docker exec -it redis-cache redis-cli GEOADD drivers 77.6500 12.9200 "driver:5"
docker exec -it redis-cache redis-cli GEOADD drivers 77.6200 12.9500 "driver:6"
docker exec -it redis-cache redis-cli GEOADD drivers 77.5700 12.9750 "driver:7"
docker exec -it redis-cache redis-cli GEOADD drivers 77.6000 12.9400 "driver:8"
```
