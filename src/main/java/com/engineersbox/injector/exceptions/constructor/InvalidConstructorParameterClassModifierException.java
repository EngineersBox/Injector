package com.engineersbox.injector.exceptions.constructor;

public class InvalidConstructorParameterClassModifierException extends RuntimeException {
    public InvalidConstructorParameterClassModifierException(final Class<?> clazz) {
        super("Constructor parameter class has invalid modifiers: " + clazz.getName());
    }
}
