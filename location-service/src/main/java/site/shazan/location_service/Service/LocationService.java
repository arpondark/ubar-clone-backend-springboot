package site.shazan.location_service.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.shazan.location_service.dtos.DriverLocationRequest;
import site.shazan.location_service.dtos.NearByDriverResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String DRIVER_GEO_KEY = "drivers";


    public void updateDriverLocation(DriverLocationRequest request) {
        log.info("Updating driver location for driverId: {}", request.getDriverId());
        Point driverPoint = new Point(
                request.getLongitude(),
                request.getLattitude()
        );
        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                driverPoint,
                request.getDriverId()
        );
        log.info("Driver location updated successfully for driverId: {}", request.getDriverId());
    }

    public List<NearByDriverResponse> findNearByDrivers(double latitude, double longitude, double radiusInKm) {
        log.info("Finding nearby drivers for lat: {}, lon: {}, radius: {}", latitude, longitude, radiusInKm);
        Circle searchArea = new Circle(new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS));
        org.springframework.data.geo.GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(DRIVER_GEO_KEY, searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending().limit(10));
        List<NearByDriverResponse> nearByDrivers = new ArrayList<>();

        if (results != null) {
            results.getContent().forEach(geoResult -> {
                RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();
                nearByDrivers.add(new NearByDriverResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        geoResult.getDistance().getValue()
                ));
            });
            log.info("Found {} nearby drivers", nearByDrivers.size());
        }

        // Ensure a non-null list is always returned and sorted by distance
        return nearByDrivers.stream()
                .sorted((d1, d2) -> Double.compare(d1.getDistanceInKm(), d2.getDistanceInKm()))
                .collect(Collectors.toList());
    }

    public void removeDriver(String driverId) {
        log.info("Removing driver from geo location: {}", driverId);
        redisTemplate.opsForZSet().remove(DRIVER_GEO_KEY, driverId);
    }
}
