package com.projeto.demo.exceptions;

public class NullIdException extends RuntimeException {

    public NullIdException() {
        super("Id cannot be null");
    }
}
