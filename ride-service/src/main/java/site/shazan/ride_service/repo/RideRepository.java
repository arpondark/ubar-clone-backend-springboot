package site.shazan.ride_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import site.shazan.ride_service.model.Ride;

public interface RideRepository extends JpaRepository<Ride, String> {
}
