package com.racetrack.racetrack;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByRacingNumber(Integer racingNumber);
}
