package com.projeto.demo.controllers;

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

    @GetMapping("/")
    public ResponseEntity<?> listTracks() {
        try {
            return ResponseEntity.ok(trackService.listTracks());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
