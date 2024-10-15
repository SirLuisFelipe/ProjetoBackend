package com.projeto.demo.controllers;

import com.projeto.demo.exceptions.UnauthorizedActionException;
import com.projeto.demo.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/track")
public class TrackController extends BaseController {

    @Autowired
    private TrackService trackService;

    @PostMapping("/")
    public ResponseEntity<?> createTrack(@RequestBody String trackName) {
        try {
            if (!isAdmin()) {
                throw new UnauthorizedActionException();
            }

            return ResponseEntity.ok(trackService.createTrack(trackName));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> listTracks() {
        try {
            return ResponseEntity.ok(trackService.listTracks());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findPaymentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(trackService.findTrackById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long id) {
        try {
            if (!isAdmin()) {
                throw new UnauthorizedActionException();
            }

            trackService.deleteTrack(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
