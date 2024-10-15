package com.projeto.demo.controllers;

import com.projeto.demo.entities.User;
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
            User user = getLoggedUser();

            if (!user.getRole().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have permission to create a track");
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
            trackService.deleteTrack(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
