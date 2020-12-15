package com.engineersbox.injector.exceptions;

import java.lang.reflect.Field;

public class FinalFieldInjectionException extends RuntimeException {
    public FinalFieldInjectionException(final Field field, final Object value) {
        super("Cannot set value of [" + value + "] on field [" + field.getName() + "]");
    }
}
