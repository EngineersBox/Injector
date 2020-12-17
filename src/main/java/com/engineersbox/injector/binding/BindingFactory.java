package com.engineersbox.injector.binding;

import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FieldValueTypeCoercionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class BindingFactory {

    private final Set<Class<?>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public BindingFactory(){
        this.requestedBindings = new HashSet<>();
    }

    public BindingFactory setInjectionSource(final String filename) {
        try {
            this.injectionSource = new ConfigurationProperties(filename);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    public BindingFactory requestStaticInjection(final Class<?> ...classes_to_bind) {
        this.requestedBindings.addAll(Arrays.asList(classes_to_bind));
        return this;
    }

    private Optional<ConfigProperty> getConfigPropertyAnnotation(final Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (!annotation.annotationType().equals(ConfigProperty.class)) {
                continue;
            }
            return Optional.of((ConfigProperty) annotation);
        }
        return Optional.empty();
    }

    private Optional<AnnotationBindingPair<Inject, String>> getInjectorAnnotations(final Field field, final Class<?> rootClass) {
        for (Annotation annotationClass : field.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotationClass.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            final Optional<ConfigProperty> configPropertyAnnotation = getConfigPropertyAnnotation(field);
            if (!configPropertyAnnotation.isPresent()) {
                throw new MissingConfigPropertyAnnotationException(rootClass);
            }
            return Optional.of(new AnnotationBindingPair<>((Inject) annotationClass, configPropertyAnnotation.get().property()));
        }
        return Optional.empty();
    }

    private <T> void setFieldWithValue(final Field field, final T value, final Class<?> clazz, final boolean optional) {
        field.setAccessible(true);
        if (!optional && value == null) {
            throw new NullObjectInjectionException(field);
        }
        try {
            field.set(clazz.newInstance(), value == null ? field.getType().newInstance() : field.getType().cast(value));
        } catch (ClassCastException | InstantiationException e) {
            throw new FieldValueTypeCoercionException(value, field.getType());
        } catch (IllegalAccessException e) {
            throw new FinalFieldInjectionException(field, value);
        }
    }

    private void saturateClassFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Optional<AnnotationBindingPair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, clazz);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final AnnotationBindingPair<Inject, String> annotationBindingPair = hasAnnotation.get();
            final String configPropertyValue = annotationBindingPair.right;
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), clazz, annotationBindingPair.left.optional());
        }
    }

    public void build() {
        for (Class<?> clazz : this.requestedBindings) {
            saturateClassFields(clazz);
        }
    }

}
