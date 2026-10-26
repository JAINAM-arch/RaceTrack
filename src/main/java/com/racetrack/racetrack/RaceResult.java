
package com.racetrack.racetrack;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"race_event_id", "driver_id"}
    )
})
public class RaceResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer finishingPosition;
    private Integer points;

    @ManyToOne
    @JoinColumn(name = "race_event_id", nullable = false)
    private RaceEvent raceEvent;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    public RaceResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFinishingPosition() {
        return finishingPosition;
    }

    public void setFinishingPosition(Integer finishingPosition) {
        this.finishingPosition = finishingPosition;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public RaceEvent getRaceEvent() {
        return raceEvent;
    }

    public void setRaceEvent(RaceEvent raceEvent) {
        this.raceEvent = raceEvent;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
