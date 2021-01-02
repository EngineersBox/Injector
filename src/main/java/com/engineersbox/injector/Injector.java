package com.engineersbox.injector;

import com.engineersbox.injector.annotations.*;
import com.engineersbox.injector.exceptions.constructor.ConstructorInjectionException;
import com.engineersbox.injector.exceptions.constructor.ConstructorParameterInvocationException;
import com.engineersbox.injector.exceptions.constructor.InvalidConstructorParameterClassModifierException;
import com.engineersbox.injector.exceptions.method.InjectedMethodInvocationException;
import com.engineersbox.injector.exceptions.method.MethodParameterInvocationException;
import com.engineersbox.injector.exceptions.module.NamedModuleBindingException;
import com.engineersbox.injector.modifiers.ModifierMapping;
import com.engineersbox.injector.modifiers.ModifierRequirement;
import com.engineersbox.injector.module.AbstractModule;
import com.engineersbox.injector.module.ModuleBinding;
import com.engineersbox.injector.module.ModuleBindingType;

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

    private Optional<Class<?>> checkForImplementedByAnnotation(final Class<?> clazz) {
        final Optional<? extends Annotation> annotation = AnnotationUtils.getAnnotation(clazz, ImplementedBy.class);
        if (!annotation.isPresent()) {
            return Optional.empty();
        }
        final ImplementedBy implementedBy = (ImplementedBy) annotation.get();
        return Optional.of(implementedBy.value());
    }

    private Object validateAndReturnFromNamedAnnotation(final Named named, final ModuleBinding moduleBinding) {
        if (named.value().equals(moduleBinding.annotatedWith.value())) {
            return moduleBinding.bindingClassInstance;
        }
        throw new NamedModuleBindingException(named, moduleBinding);
    }

    private Object instantiateMethodParameter(final Parameter parameter, final Method method) {
        final Class<?> paramClass = parameter.getType();
        final Optional<? extends Annotation> namedAnnotation = AnnotationUtils.getAnnotation(parameter, Named.class);
        if (namedAnnotation.isPresent()) {
            final Named named = (Named) namedAnnotation.get();
            final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
            if (moduleBinding.isPresent() && moduleBinding.get().validateAsBindingType(ModuleBindingType.INSTANCE_AND_ANNOTATION)) {
                return paramClass.cast(this.validateAndReturnFromNamedAnnotation(named, moduleBinding.get()));
            }
        }
        try {
            final Constructor<?> parameterConstructor = paramClass.getConstructor();
            return parameterConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            throw new MethodParameterInvocationException(paramClass);
        } catch (IllegalAccessException e) {
            throw new MethodParameterInvocationException(method);
        }
    }

    private <T> T performMethodInjectionOnInstance(final T instance, final Class<?> clazz) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (!AnnotationUtils.hasAnnotation(method, Inject.class)) {
                continue;
            }
            final List<Object> parameters = new ArrayList<>();
            for (final Parameter parameter : method.getParameters()) {
                parameters.add(this.instantiateMethodParameter(parameter, method));
            }
            try {
                method.invoke(instance, parameters.toArray());
            } catch (IllegalAccessException e) {
                throw new InjectedMethodInvocationException(method);
            } catch (InvocationTargetException e) {
                throw new InjectedMethodInvocationException(method, e);
            }
        }
        return instance;
    }

    private Object instantiateConstructorParameter(final Parameter parameter, final Constructor<?> constructor) {
        final Class<?> paramClass = parameter.getType();
        final Optional<? extends Annotation> configPropertyAnnotation = AnnotationUtils.getAnnotation(parameter, ConfigProperty.class);
        if (configPropertyAnnotation.isPresent()) {
            final ConfigProperty configProperty = (ConfigProperty) configPropertyAnnotation.get();
            return paramClass.cast(this.injectionSource.properties.getProperty(configProperty.property()));
        }
        final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
        Class<?> implementationClass;
        if (!moduleBinding.isPresent()) {
            final Optional<Class<?>> implementedBy = this.checkForImplementedByAnnotation(paramClass);
            if (!implementedBy.isPresent()) {
                return null;
            }
            implementationClass = implementedBy.get();
        } else {
            implementationClass = moduleBinding.get().implementationClass;
        }
        this.verifyInstantiable(implementationClass);
        try {
            return paramClass.cast(this.performMethodInjectionOnInstance(implementationClass.newInstance(), implementationClass));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConstructorParameterInvocationException(paramClass, constructor, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final Class<T> clazz) {
        this.module.configure();
        final Constructor<?> constructor = this.getAnnotatedConstructors(clazz);
        final List<Object> parameters = new ArrayList<>();
        for (final Parameter parameter : constructor.getParameters()) {
            parameters.add(this.instantiateConstructorParameter(parameter, constructor));
        }
        try {
            return (T) constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new ConstructorInjectionException(clazz, constructor, e.getMessage());
        }
    }
}
