package com.engineersbox.injector;

import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.InvalidConstructorParameterClassModifierException;
import com.engineersbox.injector.module.AbstractModule;
import com.engineersbox.injector.module.ModuleBinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class Injector {

    private final AbstractModule module;
    private final Set<Integer> requiredNotExists = new HashSet<>(Modifier.ABSTRACT, Modifier.INTERFACE);

    private Injector(final AbstractModule module) {
        this.module = module;
    }

    public static Injector createInjector(final AbstractModule module) {
        return new Injector(module);
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
        if (this.requiredNotExists.stream().anyMatch(i -> (modifiers & i) == 0)) {
            throw new InvalidConstructorParameterClassModifierException(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        this.module.configure();
        final Constructor<?> constructor = this.getAnnotatedConstructors(clazz);
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final List<Object> parameters = new ArrayList<>();
        for (Class<?> paramClass : parameterTypes) {
            final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
            if (moduleBinding.isPresent()) {
                final Class<?> boundToClass = moduleBinding.get().boundTo;
                this.verifyInstantiable(boundToClass);
                parameters.add(boundToClass.newInstance());
                continue;
            }
            parameters.add(null);
        }
        return (T) constructor.newInstance(parameters);
    }
}
