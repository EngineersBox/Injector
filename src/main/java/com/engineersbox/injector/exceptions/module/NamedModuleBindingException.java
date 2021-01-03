package com.engineersbox.injector.exceptions.module;

import com.engineersbox.injector.annotations.Named;
import com.engineersbox.injector.module.ModuleBinding;

public class NamedModuleBindingException extends InjectorModuleException {
    public NamedModuleBindingException(final Named named) {
        super("Binding for Named annotation [" + named + "] cannot be null or empty");
    }

    public NamedModuleBindingException(final Named named, final ModuleBinding moduleBinding) {
        super("Named annotation does not match annotation binding in ModuleBinding: " + named.value() + " != " + moduleBinding.annotatedWith.value());
    }
}
