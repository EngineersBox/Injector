package com.engineersbox.injector;

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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
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

    private boolean hasInjectAnnotation(final Annotation[] annotations) {
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private boolean hasConfigPropertyAnnotation(final Annotation[] annotations) {
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!annotationType.equals(ConfigProperty.class)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private Constructor<?> getAnnotatedConstructors(final Class<?> clazz) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (final Constructor<?> constructor : constructors) {
            final Annotation[] annotations = constructor.getDeclaredAnnotations();
            if (this.hasInjectAnnotation(annotations)) {
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

    private String getConfigPropertyValue(final Annotation[] annotations) {
        for (final Annotation annotation : annotations) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.equals(ConfigProperty.class)) {
                return ((ConfigProperty) annotation).property();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final Class<T> clazz) {
        this.module.configure();
        final Constructor<?> constructor = this.getAnnotatedConstructors(clazz);
        final List<Object> parameters = new ArrayList<>();
        for (Parameter parameter : constructor.getParameters()) {
            final Class<?> paramClass = parameter.getType();
            if (this.hasConfigPropertyAnnotation(parameter.getAnnotations())) {
                parameters.add(paramClass.cast(this.injectionSource.properties.getProperty(this.getConfigPropertyValue(parameter.getAnnotations()))));
                continue;
            }
            final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
            if (!moduleBinding.isPresent()) {
                parameters.add(null);
                continue;
            }
            final Class<?> boundToClass = moduleBinding.get().boundTo;
            this.verifyInstantiable(boundToClass);
            try {
                parameters.add(paramClass.cast(boundToClass.newInstance()));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConstructorParameterInvocationException(paramClass, constructor, e.getMessage());
            }
        }
        try {
            return (T) constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new ConstructorInjectionException(clazz, constructor, e.getMessage());
        }
    }
}
