
package com.racetrack.racetrack;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceResultRepository
        extends JpaRepository<RaceResult, Long> {

    List<RaceResult> findByRaceEventIdOrderByFinishingPositionAsc(
            Long raceEventId);
}
