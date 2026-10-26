package com.racetrack.racetrack;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository carRepository;
    private final TeamRepository teamRepository;

    public CarController(
            CarRepository carRepository,
            TeamRepository teamRepository) {
        this.carRepository = carRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addCar(@RequestBody Car car) {

        if (car.getModel() == null || car.getModel().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Car model is required.");
        }

        if (car.getTeam() == null || car.getTeam().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("A valid team ID is required.");
        }

        var team = teamRepository.findById(car.getTeam().getId());

        if (team.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("The specified team does not exist.");
        }

        car.setTeam(team.get());

        Car savedCar = carRepository.save(car);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCar);
    }
}
