package site.shazan.maching_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import site.shazan.maching_service.client.LocationServiceCLient;
import site.shazan.maching_service.dtos.NearByDriverResponse;
import site.shazan.maching_service.event.RIdeRequestedEvent;
import site.shazan.maching_service.event.RideMachedEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private static final String RIDE_MATCHING_TOPIC = "ride-mached";
    private static final double DEFAULT_SEARCH_RADIOUS_KM = 5.0;
    private final LocationServiceCLient locationServiceCLient;
    private final KafkaTemplate<String, RideMachedEvent> kafkaTemplate;

    /*
     * main matching logic
     * */

    public void matchDriverForRide(RIdeRequestedEvent event) {
        log.info("Matching driver for ride: {}", event.getRideId());

        //1. find nearby drivers using location service
        List<NearByDriverResponse> nearbyDrivers = locationServiceCLient.getNearByDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                DEFAULT_SEARCH_RADIOUS_KM
        );

        if (nearbyDrivers.isEmpty()) {
            log.info("No nearby drivers found for ride: {}", event.getRideId());
            return;
        }
        Optional<NearByDriverResponse> bestDriver = findBestDriver(nearbyDrivers);
        if (bestDriver.isEmpty()) {
            log.info("No suitable driver found for ride");
            return;
        }
        NearByDriverResponse assignedDriver = bestDriver.get();

        RideMachedEvent matchedEvent = new RideMachedEvent(
                event.getRiderId(),
                event.getRideId(),
                assignedDriver.getDriverId(),
                assignedDriver.getLattitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );
        kafkaTemplate.send(RIDE_MATCHING_TOPIC, event.getRideId(), matchedEvent);
        log.info("Ride matched for driver: {}", assignedDriver.getDriverId());


    }

    private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> nearbyDrivers) {
        double distanceWeight = 0.7;
        double ratingWeight = 0.3;
        return nearbyDrivers.stream().
                max(Comparator.comparingDouble(nearbyDriver -> {
                    double distanceScore = 1.0 / (nearbyDriver.getDistanceInKm() + 0.1);

                    double simulatedRating = 4.0 + Math.random();

                    return (distanceScore * distanceWeight) + (simulatedRating * ratingWeight);

                }));
    }


}
