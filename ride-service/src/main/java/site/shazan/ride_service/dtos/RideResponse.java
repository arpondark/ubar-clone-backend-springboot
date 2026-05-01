package site.shazan.ride_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.shazan.ride_service.model.RideStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {

    private String id;
    private String rideId;
    private String driverId;

    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropoffLatitude;
    private double dropoffLongitude;
    private String dropoffAddress;

    private RideStatus status;

    private double estimatedFare;
    private double actualFare;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
