package com.projeto.demo.controllers;

import com.projeto.demo.dto.CreateSchedulingDto;
import com.projeto.demo.entities.Scheduling;
import com.projeto.demo.services.SchedulingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController extends BaseController {

    private final SchedulingService schedulingService;

    public SchedulingController(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/")
    public ResponseEntity<Scheduling> createScheduling(@RequestBody CreateSchedulingDto createSchedulingDto) {
        try {
            Scheduling created = schedulingService.createScheduling(createSchedulingDto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/")
    public ResponseEntity<Scheduling> updateScheduling(@RequestBody CreateSchedulingDto createSchedulingDto) {
        try {
            Scheduling updated = schedulingService.updateScheduling(createSchedulingDto, getLoggedUser());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Scheduling>> listSchedulings() {
        try {
            return ResponseEntity.ok(schedulingService.listSchedulings());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Scheduling>> listSchedulingsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(schedulingService.listSchedulingsByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduling(@PathVariable Long id) {
        try {
            schedulingService.deleteScheduling(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
