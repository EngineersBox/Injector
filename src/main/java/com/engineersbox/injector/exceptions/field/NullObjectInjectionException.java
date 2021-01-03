package com.engineersbox.injector.exceptions.field;

import java.lang.reflect.Field;

public class NullObjectInjectionException extends InjectorFieldException {
    public NullObjectInjectionException(final Field field) {
        super("Cannot set null object to field: " + field.getName());
    }
}
