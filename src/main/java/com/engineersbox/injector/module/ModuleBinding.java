package com.engineersbox.injector.module;

import com.engineersbox.injector.exceptions.BoundToClassInheritanceException;

public class ModuleBinding {

    public Class<?> bindingClass;
    public Class<?> boundTo;

    public ModuleBinding(final Class<?> bindingClass, final Class<?> boundTo) {
        this.bindingClass = bindingClass;
        this.boundTo = boundTo;
    }

    public ModuleBinding(final Class<?> bindingClass) {
        this(bindingClass, null);
    }

    public void to(final Class<?> bound_to) {
        if (!this.bindingClass.isAssignableFrom(bound_to)) {
            throw new BoundToClassInheritanceException(this.bindingClass, bound_to);
        }
        this.boundTo = bound_to;
    }

}
