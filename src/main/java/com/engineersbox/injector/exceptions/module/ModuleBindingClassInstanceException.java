package com.engineersbox.injector.exceptions.module;

public class ModuleBindingClassInstanceException extends RuntimeException {
    public <T> ModuleBindingClassInstanceException(final Class<?> bindingClass, final T bindingClassInstance) {
        super("Binding class instance [" + bindingClassInstance + "] is not of type: " + bindingClass.getName());
    }
}
