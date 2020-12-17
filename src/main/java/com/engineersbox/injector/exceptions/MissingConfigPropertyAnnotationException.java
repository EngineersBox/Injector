package com.engineersbox.injector.exceptions;

public class MissingConfigPropertyAnnotationException extends RuntimeException {
    public MissingConfigPropertyAnnotationException(final Class<?> clazz) {
        super("Missing @ConfigProperty annotation alonside @Inject annotation on class: " + clazz.getCanonicalName());
    }
}
