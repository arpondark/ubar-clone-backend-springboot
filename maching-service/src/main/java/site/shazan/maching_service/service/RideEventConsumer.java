package site.shazan.maching_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import site.shazan.maching_service.event.RideRequestedEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideEventConsumer {
    private final MatchingService matchingService;

    @KafkaListener(topics = "ride-requested",groupId = "maching-service-group")
    public void consumeRideRequestedEvent(RideRequestedEvent event) {
     try {
         matchingService.matchDriverForRide(event);

     }catch (Exception e) {
         log.error("Error consuming RideRequestedEvent: {}", e.getMessage());
     }

    }
}
