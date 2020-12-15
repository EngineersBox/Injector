package com.engineersbox.injector.binding;

import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FieldValueTypeCoercionException;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;

import java.io.FileNotFoundException;
import java.io.InvalidObjectException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class BindingFactory {

    private final Set<Class<?>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public BindingFactory(){
        this.requestedBindings = new HashSet<>();
    }

    public BindingFactory setInjectionSource(final String filename) throws FileNotFoundException {
        this.injectionSource = new ConfigurationProperties(filename);
        return this;
    }

    public BindingFactory requestBinding(final Class<?> class_to_bind) {
        this.requestedBindings.add(class_to_bind);
        return this;
    }

    public BindingFactory requestBinding(final Class<?> ...classes_to_bind) {
        this.requestedBindings.addAll(Arrays.asList(classes_to_bind));
        return this;
    }

    private Optional<ConfigProperty> getConfigPropertyAnnotation(final Class<? extends Annotation> injectorAnnotation) {
        for (Annotation annotation : injectorAnnotation.getAnnotations()) {
            if (!annotation.annotationType().equals(ConfigProperty.class)) {
                continue;
            }
            return Optional.of((ConfigProperty) annotation);
        }
        return Optional.empty();
    }

    private Optional<AnnotationBindingPair<Inject, String>> hasInjectorAnnotations(final Field field, final Class<?> rootClass) {
        for (Annotation annotationClass : field.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotationClass.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            final Optional<ConfigProperty> configPropertyAnnotation = getConfigPropertyAnnotation(annotationType);
            if (!configPropertyAnnotation.isPresent()) {
                throw new MissingConfigPropertyAnnotationException(rootClass);
            }
            return Optional.of(new AnnotationBindingPair<>((Inject) annotationClass, configPropertyAnnotation.get().property()));
        }
        return Optional.empty();
    }

    private <T> void setFieldWithValue(final Field field, final T value, final Class<?> clazz) {
        field.setAccessible(true);
        if (value == null) {
            throw new NullObjectInjectionException(field);
        }
        try {
            field.set(clazz, field.getType().cast(value));
        } catch (ClassCastException e) {
            throw new FieldValueTypeCoercionException(value, field.getType());
        } catch (IllegalAccessException e) {
            throw new FinalFieldInjectionException(field, value);
        }
    }

    private void saturateClassFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Optional<AnnotationBindingPair<Inject, String>> hasAnnotation = hasInjectorAnnotations(field, clazz);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final AnnotationBindingPair<Inject, String> annotationBindingPair = hasAnnotation.get();
            final String configPropertyValue = annotationBindingPair.right;
            if (annotationBindingPair.left.optional() && (configPropertyValue == null || configPropertyValue.equalsIgnoreCase(""))) {
                continue;
            }
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), clazz);
        }
    }

    public void build() {
        for (Class<?> clazz : this.requestedBindings) {
            saturateClassFields(clazz);
        }
    }

}
