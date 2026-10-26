
package com.racetrack.racetrack;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QualifyingResultRepository
        extends JpaRepository<QualifyingResult, Long> {

    List<QualifyingResult> findByRaceEventIdOrderByLapTimeSecondsAsc(
            Long raceEventId);
}
