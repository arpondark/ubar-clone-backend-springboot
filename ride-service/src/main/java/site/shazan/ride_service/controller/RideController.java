package site.shazan.ride_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.shazan.ride_service.dtos.RideRequest;
import site.shazan.ride_service.dtos.RideResponse;
import site.shazan.ride_service.service.RideService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest request) {
        log.info("Received request for ride: {}", request.getRiderId());
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.requestRide(request));

    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getAllRides(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.getRideByRider(riderId));
    }

    @PutMapping("/{riderId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    public ResponseEntity<RideResponse> completedRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.completedRide(rideId));
    }

    public ResponseEntity<RideResponse> cancelRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.cancelRide(rideId));
    }
}
