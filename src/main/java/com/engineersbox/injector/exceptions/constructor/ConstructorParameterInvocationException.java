package com.engineersbox.injector.exceptions.constructor;

import java.lang.reflect.Constructor;

public class ConstructorParameterInvocationException extends InjectorConstructorException {
    public ConstructorParameterInvocationException(final Class<?> clazz, final Constructor<?> constructor, final String extra) {
        super("Count not instantiate parameter of type [" + clazz + "] for constructor [" + constructor + "]: " + extra);
    }
}
