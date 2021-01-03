package com.engineersbox.injector.exceptions.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InjectedMethodInvocationException extends InjectorMethodException {
    public InjectedMethodInvocationException(final Method method) {
        super("Could no invoke method [" + method.getName() + "] as it is inaccessible with modifiers: " + method.getModifiers());
    }

    public InjectedMethodInvocationException(final Method method, final InvocationTargetException e) {
        super("Method threw an exception [" + e.getTargetException() + "] whilst attempting to invoke [" + method.getName() + "]: " + e.getMessage());
    }
}
