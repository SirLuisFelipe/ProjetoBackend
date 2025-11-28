package com.projeto.demo.controllers;

import com.projeto.demo.entities.Track;
import com.projeto.demo.services.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrackControllerTest {

    @Mock
    private TrackService trackService;

    @InjectMocks
    private TrackController trackController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listTracks_ShouldReturnTracks() {
        List<Track> tracks = List.of(new Track());
        when(trackService.listTracks()).thenReturn(tracks);

        ResponseEntity<List<Track>> response = trackController.listTracks();

        assertEquals(tracks, response.getBody());
        verify(trackService).listTracks();
    }

    @Test
    void listTracks_ShouldReturnServerError_WhenServiceFails() {
        when(trackService.listTracks()).thenThrow(new RuntimeException("fail"));

        ResponseEntity<List<Track>> response = trackController.listTracks();

        assertEquals(500, response.getStatusCode().value());
    }
}
