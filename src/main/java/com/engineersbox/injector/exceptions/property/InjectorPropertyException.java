package com.engineersbox.injector.exceptions.property;

public abstract class InjectorPropertyException extends RuntimeException {
    public InjectorPropertyException(final String message) {
        super(message);
    }
}
