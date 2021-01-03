package com.engineersbox.injector.exceptions.property;

public class MissingPropertyFile extends InjectorPropertyException {
    public MissingPropertyFile(final String filename) {
        super("Could not find specified property file: " + filename);
    }
}
