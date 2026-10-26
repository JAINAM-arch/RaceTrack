package com.racetrack.racetrack;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api/status")
    public String status() {
        return "RaceTrack backend is running successfully!";
    }
}
