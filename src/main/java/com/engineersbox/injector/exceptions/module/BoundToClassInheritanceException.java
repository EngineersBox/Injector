package com.engineersbox.injector.exceptions.module;

public class BoundToClassInheritanceException extends InjectorModuleException {
    public BoundToClassInheritanceException(final Class<?> binding, final Class<?> instancing) {
        super("Instancing class [" + instancing.getName() + "] does not inherit from binding class [" + binding.getName() + "]");
    }
}
