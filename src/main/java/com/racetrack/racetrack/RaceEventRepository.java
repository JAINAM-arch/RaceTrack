
package com.racetrack.racetrack;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceEventRepository
        extends JpaRepository<RaceEvent, Long> {
}
