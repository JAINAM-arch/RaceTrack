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
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;

    public DriverController(
            DriverRepository driverRepository,
            TeamRepository teamRepository) {
        this.driverRepository = driverRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addDriver(@RequestBody Driver driver) {

        if (driver.getName() == null || driver.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Driver name is required.");
        }

        if (driver.getRacingNumber() == null
                || driver.getRacingNumber() < 1
                || driver.getRacingNumber() > 99) {
            return ResponseEntity.badRequest()
                    .body("Racing number must be between 1 and 99.");
        }

        if (driverRepository.existsByRacingNumber(
                driver.getRacingNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This racing number is already registered.");
        }

        if (driver.getTeam() == null
                || driver.getTeam().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("A valid team ID is required.");
        }

        var team = teamRepository.findById(driver.getTeam().getId());

        if (team.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("The specified team does not exist.");
        }

        driver.setTeam(team.get());

        Driver savedDriver = driverRepository.save(driver);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedDriver);
    }
}
