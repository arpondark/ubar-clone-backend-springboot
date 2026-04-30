package site.shazan.location_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String driverId;
    private double lattitude;
    private double longitude;
    private double distanceInKm;
}
