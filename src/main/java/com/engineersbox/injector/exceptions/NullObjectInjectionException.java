package com.engineersbox.injector.exceptions;

import java.lang.reflect.Field;

public class NullObjectInjectionException extends RuntimeException {
    public NullObjectInjectionException(final Field field) {
        super("Cannot set null object to field: " + field.getName());
    }
}
