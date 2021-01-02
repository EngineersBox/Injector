package com.engineersbox.injector.exceptions.method;

import java.lang.reflect.Method;

public class MethodParameterInvocationException extends RuntimeException {
    public MethodParameterInvocationException(final Class<?> paramClass) {
        super("Could not instantiate default constructor for: " + paramClass.getName());
    }

    public MethodParameterInvocationException(final Method method) {
        super("Could no invoke constructor [" + method.getName() + "] as it is inaccessible with modifiers: " + method.getModifiers());
    }
}
