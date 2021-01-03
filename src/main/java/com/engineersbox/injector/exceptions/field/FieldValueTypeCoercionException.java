package com.engineersbox.injector.exceptions.field;

public class FieldValueTypeCoercionException extends InjectorFieldException {
    public FieldValueTypeCoercionException(final Object value, final Class<?> clazz) {
        super("Could not coerce object [" + value + "] to type: " + clazz.getName());
    }
}
