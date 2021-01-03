package com.engineersbox.injector.exceptions.method;

public abstract class InjectorMethodException extends RuntimeException {
    public InjectorMethodException(final String message) {
        super(message);
    }
}
