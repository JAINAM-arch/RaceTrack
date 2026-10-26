
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
@RequestMapping("/api/results")
public class RaceResultController {

    private final RaceResultRepository resultRepository;
    private final RaceEventRepository raceEventRepository;
    private final DriverRepository driverRepository;

    public RaceResultController(
            RaceResultRepository resultRepository,
            RaceEventRepository raceEventRepository,
            DriverRepository driverRepository) {
        this.resultRepository = resultRepository;
        this.raceEventRepository = raceEventRepository;
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<RaceResult> getAllResults() {
        return resultRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addResult(@RequestBody RaceResult result) {

        if (result.getFinishingPosition() == null
                || result.getFinishingPosition() < 1) {
            return ResponseEntity.badRequest()
                    .body("Finishing position must be at least 1.");
        }

        if (result.getRaceEvent() == null
                || result.getRaceEvent().getId() == null
                || result.getDriver() == null
                || result.getDriver().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("Valid race and driver IDs are required.");
        }

        var race = raceEventRepository.findById(
                result.getRaceEvent().getId());
        var driver = driverRepository.findById(
                result.getDriver().getId());

        if (race.isEmpty() || driver.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("The specified race or driver does not exist.");
        }

        result.setRaceEvent(race.get());
        result.setDriver(driver.get());

        int position = result.getFinishingPosition();

        int points = switch (position) {
            case 1 -> 25;
            case 2 -> 18;
            case 3 -> 15;
            case 4 -> 12;
            case 5 -> 10;
            case 6 -> 8;
            case 7 -> 6;
            case 8 -> 4;
            case 9 -> 2;
            case 10 -> 1;
            default -> 0;
        };

        result.setPoints(points);

        RaceResult savedResult = resultRepository.save(result);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedResult);
    }
}
