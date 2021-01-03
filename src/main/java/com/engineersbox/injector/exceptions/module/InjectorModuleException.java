package com.engineersbox.injector.exceptions.module;

public abstract class InjectorModuleException extends RuntimeException {
    public InjectorModuleException(final String message) {
        super(message);
    }
}
