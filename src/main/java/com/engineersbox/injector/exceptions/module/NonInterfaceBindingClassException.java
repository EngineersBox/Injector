package com.engineersbox.injector.exceptions.module;

public class NonInterfaceBindingClassException extends InjectorModuleException {
    public NonInterfaceBindingClassException(final Class<?> clazz) {
        super("Binding class is not an interface: " + clazz.getName());
    }
}
