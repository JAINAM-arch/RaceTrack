
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
@RequestMapping("/api/championships")
public class ChampionshipController {

    private final ChampionshipRepository championshipRepository;

    public ChampionshipController(
            ChampionshipRepository championshipRepository) {
        this.championshipRepository = championshipRepository;
    }

    @GetMapping
    public List<Championship> getAllChampionships() {
        return championshipRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addChampionship(
            @RequestBody Championship championship) {

        if (championship.getName() == null
                || championship.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Championship name is required.");
        }

        if (championship.getSeasonYear() == null
                || championship.getSeasonYear() < 1950
                || championship.getSeasonYear() > 2100) {
            return ResponseEntity.badRequest()
                    .body("Enter a valid championship season year.");
        }

        if (championship.getStatus() == null
                || championship.getStatus().isBlank()) {
            championship.setStatus("UPCOMING");
        }

        Championship savedChampionship =
                championshipRepository.save(championship);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedChampionship);
    }
}
