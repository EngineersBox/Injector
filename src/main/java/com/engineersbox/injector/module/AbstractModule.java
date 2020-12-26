package com.engineersbox.injector.module;

import com.engineersbox.injector.exceptions.NonInterfaceBindingClassException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractModule {

    Set<ModuleBinding> moduleBindings = new HashSet<>();

    public abstract void configure();

    public ModuleBinding bind(final Class<?> binding_class) {
        if (!binding_class.isInterface()) {
            throw new NonInterfaceBindingClassException(binding_class);
        }
        if (this.hasBindingFor(binding_class)) {
            // TODO: Throw error here for pre-existing binding
            return null;
        }
        final ModuleBinding binding = new ModuleBinding(binding_class);
        this.moduleBindings.add(binding);
        return binding;
    }

    public boolean hasBindingFor(final Class<?> clazz) {
        return this.moduleBindings.stream()
            .anyMatch(moduleBinding -> moduleBinding.bindingClass == clazz);
    }

    public Optional<ModuleBinding> getModuleBindingForBindingClass(final Class<?> clazz) {
        return this.moduleBindings.stream()
            .filter(moduleBinding -> moduleBinding.bindingClass == clazz)
            .findFirst();
    }

}
