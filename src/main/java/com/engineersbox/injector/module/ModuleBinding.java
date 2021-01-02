package com.engineersbox.injector.module;

import com.engineersbox.injector.annotations.Named;
import com.engineersbox.injector.exceptions.BoundToClassInheritanceException;

public class ModuleBinding {

    public Class<?> bindingClass;
    public Class<?> implementationClass;
    public Named annotatedWith;
    public Object bindingClassInstance;

    public ModuleBinding(final Class<?> bindingClass, final Class<?> implementationClass) {
        this.bindingClass = bindingClass;
        this.implementationClass = implementationClass;
    }

    public ModuleBinding(final Class<?> bindingClass) {
        this(bindingClass, null);
    }

    public void to(final Class<?> implementationClass) {
        if (!this.bindingClass.isAssignableFrom(implementationClass)) {
            throw new BoundToClassInheritanceException(this.bindingClass, implementationClass);
        }
        this.implementationClass = implementationClass;
    }

    public ModuleBinding annotatedWith(final Named annotatedWith) {
        // TODO: Check that there is a name stored in the Named instance
        this.annotatedWith = annotatedWith;
        return this;
    }

    public <T> void toInstance(final T bindingInstanceValue) {
        try {
            this.bindingClassInstance = bindingClass.cast(bindingInstanceValue);
        } catch (ClassCastException e) {
            // TODO: Throw custom exception
        }
    }

}
