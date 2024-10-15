package com.projeto.demo.services;

import com.projeto.demo.entities.Track;
import com.projeto.demo.exceptions.TrackNotFoundException;
import com.projeto.demo.repositories.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @InjectMocks
    private TrackService trackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrack_ShouldSaveTrack() {
        Track track = new Track();
        track.setName("Test Track");

        when(trackRepository.save(any(Track.class))).thenReturn(track);

        Track result = trackService.createTrack("Test Track");
        assertNotNull(result);
        verify(trackRepository, times(1)).save(any(Track.class));
    }

    @Test
    void findTrackById_ShouldThrowException_WhenNotFound() {
        Long id = 1L;
        when(trackRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class, () -> trackService.findTrackById(id));
    }
}
