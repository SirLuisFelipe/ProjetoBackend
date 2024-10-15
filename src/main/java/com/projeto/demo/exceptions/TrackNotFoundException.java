package com.projeto.demo.exceptions;

public class TrackNotFoundException extends RuntimeException {

    public TrackNotFoundException() {
        super("Track not found");
    }
}
