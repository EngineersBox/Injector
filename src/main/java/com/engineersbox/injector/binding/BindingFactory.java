package com.engineersbox.injector.binding;

import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public abstract class BindingFactory {

    abstract BindingFactory setInjectionSource(final String filename);

    Optional<ConfigProperty> getConfigPropertyAnnotation(final Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (!annotation.annotationType().equals(ConfigProperty.class)) {
                continue;
            }
            return Optional.of((ConfigProperty) annotation);
        }
        return Optional.empty();
    }

    Optional<Pair<Inject, String>> getInjectorAnnotations(final Field field, final Class<?> rootClass) {
        for (Annotation annotationClass : field.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotationClass.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            final Optional<ConfigProperty> configPropertyAnnotation = getConfigPropertyAnnotation(field);
            if (!configPropertyAnnotation.isPresent()) {
                throw new MissingConfigPropertyAnnotationException(rootClass);
            }
            return Optional.of(new Pair<>((Inject) annotationClass, configPropertyAnnotation.get().property()));
        }
        return Optional.empty();
    }

    abstract void build();

}
