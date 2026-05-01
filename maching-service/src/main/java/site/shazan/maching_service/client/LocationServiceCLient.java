package site.shazan.maching_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import site.shazan.maching_service.dtos.NearByDriverResponse;

import java.util.List;

@FeignClient(name = "location-service", url = "${location-service.url}")
@Service
public interface LocationServiceCLient {
    List<NearByDriverResponse> getNearByDrivers(@RequestParam double lattitude, @RequestParam double longitude, @RequestParam double radius);
}
