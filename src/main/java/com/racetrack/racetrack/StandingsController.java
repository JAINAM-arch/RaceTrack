
package com.racetrack.racetrack;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/standings")
public class StandingsController {

    private final RaceResultRepository resultRepository;

    public StandingsController(RaceResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getStandings(
            @RequestParam(required = false) Long championshipId) {

        List<RaceResult> results = resultRepository.findAll();

        if (championshipId != null) {
            results = results.stream()
                    .filter(result -> result.getRaceEvent()
                            .getChampionship().getId().equals(championshipId))
                    .toList();
        }

        Map<Long, Map<String, Object>> standings = new LinkedHashMap<>();

        for (RaceResult result : results) {
            Driver driver = result.getDriver();
            Long driverId = driver.getId();

            Map<String, Object> row = standings.computeIfAbsent(
                    driverId, id -> {
                        Map<String, Object> entry = new LinkedHashMap<>();
                        entry.put("driverId", driverId);
                        entry.put("driverName", driver.getName());
                        entry.put("teamName", driver.getTeam().getName());
                        entry.put("totalPoints", 0);
                        return entry;
                    });

            int total = (int) row.get("totalPoints");
            row.put("totalPoints", total + result.getPoints());
        }

        List<Map<String, Object>> sortedStandings =
                standings.values().stream()
                        .sorted(Comparator.comparingInt(
                                (Map<String, Object> row) ->
                                        (int) row.get("totalPoints"))
                                .reversed())
                        .toList();

        int position = 1;
        for (Map<String, Object> row : sortedStandings) {
            row.put("position", position++);
        }

        return sortedStandings;
    }
}
