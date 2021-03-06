package com.engineersbox.injector.exceptions.field;

import java.lang.reflect.Field;

public class FinalFieldInjectionException extends InjectorFieldException {
    public FinalFieldInjectionException(final Field field, final Object value) {
        super("Cannot set value of [" + value + "] on field [" + field.getName() + "]");
    }
}
