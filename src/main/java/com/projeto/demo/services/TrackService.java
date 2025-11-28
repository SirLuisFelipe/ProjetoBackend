package com.projeto.demo.services;

import com.projeto.demo.entities.Track;
import com.projeto.demo.exceptions.TrackNotFoundException;
import com.projeto.demo.repositories.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService {

    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<Track> listTracks() {
        return trackRepository.findAll();
    }

    public Track findTrackById(Long id) {
        return trackRepository.findById(id).orElseThrow(TrackNotFoundException::new);
    }

}
