package com.projeto.demo.services;

import com.projeto.demo.entities.Track;
import com.projeto.demo.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    public Track createTrack(String name) {
        Track track = new Track();
        track.setName(name);

        return trackRepository.save(track);
    }

    public List<Track> listTracks() {
        return trackRepository.findAll();
    }

    public Track findTrackById(Long id) {
        return trackRepository.findById(id).orElseThrow(() -> new RuntimeException("Track not found"));
    }

    public void deleteTrack(Long id) {
        trackRepository.deleteById(id);
    }

}
