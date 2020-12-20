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

public class StaticBindingFactory extends BindingFactory {

    private final Set<Class<?>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public StaticBindingFactory(){
        this.requestedBindings = new HashSet<>();
    }

    @Override
    public StaticBindingFactory setInjectionSource(final String filename) {
        try {
            this.injectionSource = new ConfigurationProperties(filename);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    public StaticBindingFactory requestInjection(final Class<?> ...classes_to_bind) {
        this.requestedBindings.addAll(Arrays.asList(classes_to_bind));
        return this;
    }

    private <T> void setFieldWithValue(final Field field, final T value, final Class<?> clazz, final boolean optional) {
        if (!optional && value == null) {
            throw new NullObjectInjectionException(field);
        }
        field.setAccessible(true);
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
            Optional<Pair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, clazz);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final Pair<Inject, String> pair = hasAnnotation.get();
            final String configPropertyValue = pair.right;
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), clazz, pair.left.optional());
        }
    }

    @Override
    public void build() {
        for (Class<?> clazz : this.requestedBindings) {
            saturateClassFields(clazz);
        }
    }

}
