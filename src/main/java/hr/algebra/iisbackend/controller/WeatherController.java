package hr.algebra.iisbackend.controller;

import hr.algebra.iisbackend.grpc.CityRequest;
import hr.algebra.iisbackend.grpc.CityTemperature;
import hr.algebra.iisbackend.grpc.TemperatureResponse;
import hr.algebra.iisbackend.grpc.WeatherServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping("/{city}")
    public ResponseEntity<?> getTemperature(@PathVariable String city) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        WeatherServiceGrpc.WeatherServiceBlockingStub stub =
                WeatherServiceGrpc.newBlockingStub(channel);

        CityRequest request = CityRequest.newBuilder()
                .setCityName(city)
                .build();

        TemperatureResponse response = stub.getTemperature(request);
        channel.shutdown();

        List<Map<String, String>> result = response.getCitiesList().stream()
                .map(c -> Map.of("grad", c.getCityName(), "temperatura", c.getTemperature()))
                .toList();

        return ResponseEntity.ok(result);
    }
}
