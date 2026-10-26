
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
@RequestMapping("/api/races")
public class RaceEventController {

    private final RaceEventRepository raceEventRepository;
    private final ChampionshipRepository championshipRepository;

    public RaceEventController(
            RaceEventRepository raceEventRepository,
            ChampionshipRepository championshipRepository) {
        this.raceEventRepository = raceEventRepository;
        this.championshipRepository = championshipRepository;
    }

    @GetMapping
    public List<RaceEvent> getAllRaces() {
        return raceEventRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addRace(@RequestBody RaceEvent race) {

        if (race.getName() == null || race.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Race name is required.");
        }

        if (race.getChampionship() == null
                || race.getChampionship().getId() == null) {
            return ResponseEntity.badRequest()
                    .body("A valid championship ID is required.");
        }

        var championship = championshipRepository.findById(
                race.getChampionship().getId());

        if (championship.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("The specified championship does not exist.");
        }

        race.setChampionship(championship.get());

        if (race.getStatus() == null || race.getStatus().isBlank()) {
            race.setStatus("UPCOMING");
        }

        RaceEvent savedRace = raceEventRepository.save(race);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedRace);
    }
}
