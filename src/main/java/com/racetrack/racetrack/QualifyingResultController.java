
package com.racetrack.racetrack;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qualifying")
public class QualifyingResultController {

    private final QualifyingResultRepository qualifyingRepository;
    private final RaceEventRepository raceEventRepository;
    private final DriverRepository driverRepository;

    public QualifyingResultController(
            QualifyingResultRepository qualifyingRepository,
            RaceEventRepository raceEventRepository,
            DriverRepository driverRepository) {
        this.qualifyingRepository = qualifyingRepository;
        this.raceEventRepository = raceEventRepository;
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<QualifyingResult> getQualifyingResults(
            @RequestParam Long raceId) {
        return qualifyingRepository
                .findByRaceEventIdOrderByLapTimeSecondsAsc(raceId);
    }

    @PostMapping
    public ResponseEntity<?> addQualifyingResult(
            @RequestBody QualifyingResult result) {

        if (result.getLapTimeSeconds() == null
                || !Double.isFinite(result.getLapTimeSeconds())
                || result.getLapTimeSeconds() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Lap time must be a positive number.");
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

        QualifyingResult saved =
                qualifyingRepository.save(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    

    @PostMapping("/calculate-grid")
    public ResponseEntity<?> calculateGrid(@RequestParam Long raceId) {

        var race = raceEventRepository.findById(raceId);

        if (race.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("The specified race does not exist.");
        }

        List<QualifyingResult> results = qualifyingRepository
                .findByRaceEventIdOrderByLapTimeSecondsAsc(raceId);

        if (results.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No qualifying results found for this race.");
        }

        for (int i = 0; i < results.size(); i++) {
            results.get(i).setGridPosition(i + 1);
        }

        qualifyingRepository.saveAll(results);

        return ResponseEntity.ok(results);
    }

}
