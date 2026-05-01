package site.shazan.ride_service.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String rideId;
    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false)
    private double picakupLatitude;
    @Column(nullable = false)
    private double pickupLontitude;
    private String pickupAddress;
    private double dropoffLatitude;
    private double dropoffLontitude;
    private String dropoffAddress;
    private RideStatus status;

    private double estimatedFare;
    private double actualFare;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;


}
