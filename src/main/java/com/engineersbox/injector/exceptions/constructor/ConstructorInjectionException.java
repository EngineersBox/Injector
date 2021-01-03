package com.engineersbox.injector.exceptions.constructor;

import java.lang.reflect.Constructor;

public class ConstructorInjectionException extends InjectorConstructorException {
    public ConstructorInjectionException(final Class<?> clazz, final Constructor<?> constructor, final String extra) {
        super("Could not instantiate [" + clazz.getName() + "] with constructor [" + constructor + "]: " + extra);
    }
}
