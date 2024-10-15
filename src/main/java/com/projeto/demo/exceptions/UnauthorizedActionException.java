package com.projeto.demo.exceptions;

public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException() {
        super("Unauthorized action");
    }
}
