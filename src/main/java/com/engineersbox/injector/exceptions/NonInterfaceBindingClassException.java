package com.engineersbox.injector.exceptions;

public class NonInterfaceBindingClassException extends RuntimeException {
    public NonInterfaceBindingClassException(final Class<?> clazz) {
        super("Binding class is not an interface: " + clazz.getName());
    }
}
