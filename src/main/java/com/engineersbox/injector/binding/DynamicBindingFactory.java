package com.engineersbox.injector.binding;

import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FieldValueTypeCoercionException;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;

import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DynamicBindingFactory extends BindingFactory{

    private final Set<Pair<Class<?>, Object>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public DynamicBindingFactory() {
        this.requestedBindings = new HashSet<>();
    }

    @Override
    public DynamicBindingFactory setInjectionSource(String filename) {
        try {
            this.injectionSource = new ConfigurationProperties(filename);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    public DynamicBindingFactory requestInjection(final Class<?> class_to_bind, final Object object_to_bind) {
        this.requestedBindings.add(new Pair<>(class_to_bind, object_to_bind));
        return this;
    }

    private <T> void setFieldWithValue(final Field field, final T value, final Pair<Class<?>, Object> binding_pair, final boolean optional) {
        if (!optional && value == null) {
            throw new NullObjectInjectionException(field);
        }
        if (Modifier.isFinal(field.getModifiers())) {
            throw new FinalFieldInjectionException(field, value);
        }
        field.setAccessible(true);
        try {
            field.set(binding_pair.left.cast(binding_pair.right), value == null ? field.getType().newInstance() : field.getType().cast(value));
        } catch (ClassCastException | InstantiationException e) {
            throw new FieldValueTypeCoercionException(value, field.getType());
        } catch (IllegalAccessException e) {
            throw new FinalFieldInjectionException(field, value);
        }
    }

    private void saturateClassFields(final Pair<Class<?>, Object> binding_pair) {
        final Field[] fields = binding_pair.left.getDeclaredFields();
        for (Field field : fields) {
            Optional<Pair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, binding_pair.left);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final Pair<Inject, String> pair = hasAnnotation.get();
            final String configPropertyValue = pair.right;
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), binding_pair, pair.left.optional());
        }
    }

    @Override
    public void build() {
        for (Pair<Class<?>, Object> bindingPair : this.requestedBindings) {
            saturateClassFields(bindingPair);
        }
    }
}
