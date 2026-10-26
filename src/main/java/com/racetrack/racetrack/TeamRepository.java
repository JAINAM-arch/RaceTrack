package com.racetrack.racetrack;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
	boolean existsByNameIgnoreCase(String name);
}
