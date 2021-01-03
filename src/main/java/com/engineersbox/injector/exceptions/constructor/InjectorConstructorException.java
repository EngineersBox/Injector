package com.engineersbox.injector.exceptions.constructor;

public abstract class InjectorConstructorException extends RuntimeException {
    public InjectorConstructorException(final String message) {
        super(message);
    }
}
