package site.shazan.ride_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import site.shazan.ride_service.dtos.RideRequest;
import site.shazan.ride_service.dtos.RideResponse;
import site.shazan.ride_service.event.RideRequestEvent;
import site.shazan.ride_service.model.Ride;
import site.shazan.ride_service.model.RideStatus;
import site.shazan.ride_service.repo.RideRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestEvent> kafkaTemplate;
    private static final String RIDE_REQUEST_TOPIC = "ride-request";


    public RideResponse requestRide(RideRequest request) {
        log.info("Received request for ride: {}", request.getRiderId());


        //1. save to database and generate rideId
        Ride ride = new Ride();
        ride.setRideId(request.getRiderId());
        ride.setPicakupLatitude(request.getPickupLatitude());
        ride.setPickupLontitude(request.getPickupLongitude());
        ride.setDropoffLatitude(request.getDropoffLatitude());
        ride.setDropoffLontitude(request.getDropoffLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropoffAddress(request.getDropoffAddress());
        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculatedEstimatedFare(request));
        Ride savedRide = rideRepository.save(ride);

        //2. publish ride request event to kafka
        RideRequestEvent event = new RideRequestEvent(
                savedRide.getRideId(),
                savedRide.getPicakupLatitude(),
                savedRide.getPickupLontitude(),
                savedRide.getDropoffLatitude(),
                savedRide.getDropoffLontitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropoffAddress(),
                savedRide.getEstimatedFare()
        );
        kafkaTemplate.send(RIDE_REQUEST_TOPIC, event);
        log.info("Ride request event published to kafka {}",savedRide.getId());

        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);
        return mapToRespose(savedRide);

    }

    private RideResponse mapToRespose(Ride ride) {
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setRideId(ride.getRideId());
        response.setDriverId(ride.getDriverId());
        response.setPickupLatitude(ride.getPicakupLatitude());
        response.setPickupLongitude(ride.getPickupLontitude());
        response.setPickupAddress(ride.getPickupAddress());
        response.setDropoffLatitude(ride.getDropoffLatitude());
        response.setDropoffLongitude(ride.getDropoffLontitude());
        response.setDropoffAddress(ride.getDropoffAddress());
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());
        response.setStatus(ride.getStatus());
        response.setCreatedAt(ride.getCreatedAt());
        response.setUpdatedAt(ride.getUpdatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());
        return response;

    }

    public void updateRideStatus(String rideId, String driverId) {
        Ride ride =rideRepository.findById(rideId)
                .orElseThrow(()->new RuntimeException("Ride not found with id: "+rideId));
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }

    private double calculatedEstimatedFare(RideRequest request) {
        double lat1 = Math.toRadians(request.getPickupLatitude());
        double lat2 = Math.toRadians(request.getDropoffLatitude());

        double lon1 = Math.toRadians(request.getPickupLongitude());
        double lon2 = Math.toRadians(request.getDropoffLongitude());

        // Differences
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Earth radius in KM
        double distanceKm = 6371 * c;

        // Base fare: 50 + 12 per KM
        double fare = 50 + (distanceKm * 12);

        // Round to 2 decimal places
        return Math.round(fare * 100.0) / 100.0;

    }

    public RideResponse getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .map(this::mapToRespose)
                .orElseThrow(()->new RuntimeException("Ride not found with id: "+rideId));
    }

    public List<RideResponse> getRideByRider(String riderId) {
        Ride ride = rideRepository.findById(riderId).orElseThrow(()->new RuntimeException("Ride not found with id: "+riderId));
        return Collections.singletonList(mapToRespose(ride));

    }

    public RideResponse startRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(()->new RuntimeException("Ride not found with id: "+rideId));
        if(ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride is not accepted yet");
        }
        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);
        return mapToRespose(ride);
    }


    public RideResponse cancelRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(()->new RuntimeException("Ride not found with id: "+rideId));
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
        return mapToRespose(ride);
    }

    public RideResponse completedRide(String rideId) {
        Ride ride= rideRepository.findById(rideId)
                .orElseThrow(()->new RuntimeException("Ride not found with id: "+rideId));
        if(ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException(String.format("Ride is not completed yet. status %s", ride.getStatus()));
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());
        rideRepository.save(ride);
        return mapToRespose(ride);
    }
}
