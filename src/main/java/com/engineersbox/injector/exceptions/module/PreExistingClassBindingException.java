package com.engineersbox.injector.exceptions.module;

public class PreExistingClassBindingException extends InjectorModuleException {
    public PreExistingClassBindingException(final Class<?> bind_from, final Class<?> binding_to) {
        super("Binding for [" + bind_from.getName() + "] already exists: " + binding_to.getName());
    }
}
