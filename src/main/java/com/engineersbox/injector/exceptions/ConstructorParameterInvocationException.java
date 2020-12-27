package com.engineersbox.injector.exceptions;

import java.lang.reflect.Constructor;

public class ConstructorParameterInvocationException extends RuntimeException {
    public ConstructorParameterInvocationException(final Class<?> clazz, final Constructor<?> constructor, final String extra) {
        super("Count not instantiate parameter of type [" + clazz + "] for constructor [" + constructor + "]: " + extra);
    }
}
