package com.engineersbox.injector.binding;

import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.group.InjectionGroup;
import com.engineersbox.injector.modifiers.ModifierMapping;
import com.engineersbox.injector.modifiers.ModifierRequirement;

import java.lang.reflect.Modifier;
import java.util.*;

public class StaticBindingFactory extends BindingFactory {

    private final Set<InjectionGroup> requestedBindings;

    public StaticBindingFactory(){
        this.requestedBindings = new HashSet<>();
        this.modifierRequirement = new ModifierRequirement()
            .setMustExist(ModifierMapping.STATIC)
            .setMustNotExist(ModifierMapping.FINAL);
    }

    @Override
    public StaticBindingFactory setInjectionSource(final String filename) {
        this.injectionSource = new ConfigurationProperties(filename);
        return this;
    }

    @Override
    public StaticBindingFactory requestInjection(final InjectionGroup ...bindings) {
        this.requestedBindings.addAll(Arrays.asList(bindings));
        return this;
    }

    @Override
    public void build() {
        for (final InjectionGroup injectionGroup : this.requestedBindings) {
            saturateClassFields(injectionGroup);
        }
    }

}
