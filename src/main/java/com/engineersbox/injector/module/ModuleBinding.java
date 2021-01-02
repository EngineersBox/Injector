package com.engineersbox.injector.module;

import com.engineersbox.injector.annotations.Named;
import com.engineersbox.injector.exceptions.module.BoundToClassInheritanceException;
import com.engineersbox.injector.exceptions.module.ModuleBindingClassInstanceException;
import com.engineersbox.injector.exceptions.module.NamedModuleBindingException;
import org.apache.commons.lang3.StringUtils;

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
        if (StringUtils.isEmpty(annotatedWith.value())) {
            throw new NamedModuleBindingException(annotatedWith);
        }
        this.annotatedWith = annotatedWith;
        return this;
    }

    public <T> void toInstance(final T bindingInstanceValue) {
        try {
            this.bindingClassInstance = this.bindingClass.cast(bindingInstanceValue);
        } catch (ClassCastException e) {
            throw new ModuleBindingClassInstanceException(this.bindingClass, bindingInstanceValue);
        }
    }

    public boolean validateAsBindingType(final ModuleBindingType bindingType) {
        switch (bindingType) {
            case INTERFACE_AND_IMPLEMENTATION:
                return this.bindingClass != null
                        && this.bindingClass.isInterface()
                        && this.implementationClass.isAssignableFrom(this.bindingClass);
            case INSTANCE_AND_ANNOTATION:
                return this.bindingClass != null
                        && !this.bindingClass.isInterface()
                        && this.bindingClass.isInstance(this.bindingClassInstance)
                        && this.annotatedWith != null;
            default:
                return false;
        }
    }

}
