package com.projeto.demo.exceptions;

public class SchedulingNotFoundException extends RuntimeException {

    public SchedulingNotFoundException() {
        super("Scheduling not found");
    }
}
