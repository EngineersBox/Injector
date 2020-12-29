package com.engineersbox.injector;

import com.engineersbox.injector.annotations.AnnotationUtils;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.ConstructorInjectionException;
import com.engineersbox.injector.exceptions.ConstructorParameterInvocationException;
import com.engineersbox.injector.exceptions.InvalidConstructorParameterClassModifierException;
import com.engineersbox.injector.modifiers.ModifierMapping;
import com.engineersbox.injector.modifiers.ModifierRequirement;
import com.engineersbox.injector.module.AbstractModule;
import com.engineersbox.injector.module.ModuleBinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class Injector {

    private final AbstractModule module;
    private final ModifierRequirement modifierRequirement = new ModifierRequirement().setMustNotExist(ModifierMapping.ABSTRACT, ModifierMapping.INTERFACE);
    private ConfigurationProperties injectionSource;

    private Injector(final AbstractModule module) {
        this.module = module;
    }

    public static Injector createInjector(final AbstractModule module) {
        return new Injector(module);
    }

    public Injector setInjectionSource(final String filename) {
        this.injectionSource = new ConfigurationProperties(filename);
        return this;
    }

    private Constructor<?> getAnnotatedConstructors(final Class<?> clazz) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (final Constructor<?> constructor : constructors) {
            if (AnnotationUtils.hasAnnotation(constructor, Inject.class)) {
                return constructor;
            }
        }
        return constructors[0];
    }

    private void verifyInstantiable(final Class<?> clazz) {
        final int modifiers = clazz.getModifiers();
        if (!this.modifierRequirement.assertModifierCombination(modifiers)) {
            throw new InvalidConstructorParameterClassModifierException(clazz);
        }
    }

    private Object instantiateParameter(final Parameter parameter, final Constructor<?> constructor) {
        final Class<?> paramClass = parameter.getType();
        final Optional<? extends Annotation> configPropertyAnnotation = AnnotationUtils.getAnnotation(parameter, ConfigProperty.class);
        if (configPropertyAnnotation.isPresent()) {
            final ConfigProperty configProperty = (ConfigProperty) configPropertyAnnotation.get();
            return paramClass.cast(this.injectionSource.properties.getProperty(configProperty.property()));
        }
        final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
        if (!moduleBinding.isPresent()) {
            return null;
        }
        final Class<?> boundToClass = moduleBinding.get().boundTo;
        this.verifyInstantiable(boundToClass);
        try {
            return paramClass.cast(boundToClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConstructorParameterInvocationException(paramClass, constructor, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final Class<T> clazz) {
        this.module.configure();
        final Constructor<?> constructor = this.getAnnotatedConstructors(clazz);
        final List<Object> parameters = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            parameters.add(this.instantiateParameter(parameter, constructor));
        }
        try {
            return (T) constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new ConstructorInjectionException(clazz, constructor, e.getMessage());
        }
    }
}
