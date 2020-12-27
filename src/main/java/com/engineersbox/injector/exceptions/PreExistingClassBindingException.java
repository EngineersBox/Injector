package com.engineersbox.injector.exceptions;

public class PreExistingClassBindingException extends RuntimeException {
    public PreExistingClassBindingException(final Class<?> bind_from, final Class<?> binding_to) {
        super("Binding for [" + bind_from.getName() + "] already exists: " + binding_to.getName());
    }
}
