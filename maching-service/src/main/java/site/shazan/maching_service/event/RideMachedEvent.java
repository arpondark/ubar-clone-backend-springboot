package site.shazan.maching_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMachedEvent {
    private String riderId;
    private String rideId;
    private String driverId;
    private double driverLatitude;
    private double driverLongitude;
    private double disthanseToPickupKm;
}
