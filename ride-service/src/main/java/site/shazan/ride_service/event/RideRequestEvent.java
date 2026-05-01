package site.shazan.ride_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {
    private String rideId;
    private String getRideId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    private double dropoffLatitude;
    private double dropoffLongitude;
    private String dropoffAddress;

    public RideRequestEvent(String rideId, double picakupLatitude, double pickupLontitude, double dropoffLatitude, double dropoffLontitude, String pickupAddress, String dropoffAddress, double estimatedFare) {
        this.rideId = rideId;
        this.pickupLatitude = picakupLatitude;
        this.pickupLongitude = pickupLontitude;
        this.pickupAddress = pickupAddress;
        this.dropoffLatitude = dropoffLatitude;
        this.dropoffLongitude = dropoffLontitude;
        this.dropoffAddress = dropoffAddress;
    }
}
