package com.engineersbox.injector.exceptions;

public class MissingPropertyFile extends RuntimeException {
    public MissingPropertyFile(final String filename) {
        super("Could not find specified property file: " + filename);
    }
}
