package com.engineersbox.injector.module;

import com.engineersbox.injector.exceptions.NonInterfaceBindingClassException;
import com.engineersbox.injector.exceptions.PreExistingClassBindingException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractModule {

    Set<ModuleBinding> moduleBindings = new HashSet<>();

    public abstract void configure();

    private void validateBinding(final Class<?> binding_class) {
        if (!binding_class.isInterface()) {
            throw new NonInterfaceBindingClassException(binding_class);
        }
        final Optional<ModuleBinding> binding_to_existing = this.getModuleBindingForBindingClass(binding_class);
        if (binding_to_existing.isPresent()) {
            throw new PreExistingClassBindingException(binding_class, binding_to_existing.get().boundTo);
        }
    }

    public ModuleBinding bind(final Class<?> binding_class) {
        this.validateBinding(binding_class);
        final ModuleBinding binding = new ModuleBinding(binding_class);
        this.moduleBindings.add(binding);
        return binding;
    }

    public Optional<ModuleBinding> getModuleBindingForBindingClass(final Class<?> clazz) {
        return this.moduleBindings.stream()
            .filter(moduleBinding -> moduleBinding.bindingClass == clazz)
            .findFirst();
    }

}
