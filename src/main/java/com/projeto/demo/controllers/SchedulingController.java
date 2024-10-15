package com.projeto.demo.controllers;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.services.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    private SchedulingService schedulingService;

    @PostMapping("/")
    public ResponseEntity<?> createScheduling(@RequestBody CreateSchedulingDto createSchedulingDto) {
        try {
            return ResponseEntity.ok(schedulingService.createScheduling(createSchedulingDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateScheduling(@RequestBody CreateSchedulingDto createSchedulingDto) {
        try {
            return ResponseEntity.ok(schedulingService.updateScheduling(createSchedulingDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listSchedulings() {
        try {
            return ResponseEntity.ok(schedulingService.listSchedulings());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listSchedulingsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(schedulingService.listSchedulingsByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<?> listSchedulingsByTrack(@PathVariable Long trackId) {
        try {
            return ResponseEntity.ok(schedulingService.listSchedulingsByTrackId(trackId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findSchedulingById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(schedulingService.findSchedulingById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScheduling(@PathVariable Long id) {
        try {
            schedulingService.deleteScheduling(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
