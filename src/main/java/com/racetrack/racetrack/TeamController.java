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
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @GetMapping
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addTeam(@RequestBody Team team) {

        if (team.getName() == null || team.getName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Team name is required.");
        }

        if (teamRepository.existsByNameIgnoreCase(team.getName().trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A team with this name already exists.");
        }

        team.setName(team.getName().trim());

        Team savedTeam = teamRepository.save(team);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedTeam);
    }
}
