package site.shazan.location_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shazan.location_service.Service.LocationService;
import site.shazan.location_service.dtos.DriverLocationRequest;
import site.shazan.location_service.dtos.NearByDriverResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocationRequest request) {
        locationService.updateDriverLocation(request);
        return ResponseEntity.ok("Driver location updated successfully");
    }

    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearbyDriverLocation(@RequestParam double lattitude, @RequestParam double longitude, @RequestParam(defaultValue = "5.0") double radius) {
        return ResponseEntity.ok(locationService.findNearByDrivers(lattitude, longitude, radius));
    }

    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId) {
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }

}
/*
* all is in redis cache not in real database
*
* */
