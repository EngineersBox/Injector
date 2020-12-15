package com.engineersbox.injector.exceptions;

public class FieldValueTypeCoercionException extends RuntimeException{
    public FieldValueTypeCoercionException(final Object value, final Class<?> clazz) {
        super("Could not coerce object [" + value + "] to type: " + clazz.getName());
    }
}
