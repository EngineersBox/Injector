package com.engineersbox.injector.exceptions;

import org.jetbrains.annotations.NotNull;

public class MissingConfigPropertyAnnotationException extends RuntimeException {
    public MissingConfigPropertyAnnotationException(final @NotNull Class<?> clazz) {
        super("Missing @ConfigProperty annotation alonside @Inject annotation on class: " + clazz.getCanonicalName());
    }
}
