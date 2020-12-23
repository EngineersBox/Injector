package com.engineersbox.injector.binding;

import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.group.InjectionGroup;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FieldValueTypeCoercionException;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public abstract class BindingFactory {

    List<Integer> modifiersRequiredToExist;
    List<Integer> modifiersRequiredToNotExist;
    ConfigurationProperties injectionSource;

    abstract BindingFactory setInjectionSource(final String filename);

    Optional<ConfigProperty> getConfigPropertyAnnotation(final Field field) {
        for (final Annotation annotation : field.getAnnotations()) {
            if (!annotation.annotationType().equals(ConfigProperty.class)) {
                continue;
            }
            return Optional.of((ConfigProperty) annotation);
        }
        return Optional.empty();
    }

    Optional<Pair<Inject, String>> getInjectorAnnotations(final Field field, final Class<?> rootClass) {
        for (final Annotation annotationClass : field.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotationClass.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            final Optional<ConfigProperty> configPropertyAnnotation = getConfigPropertyAnnotation(field);
            if (!configPropertyAnnotation.isPresent()) {
                throw new MissingConfigPropertyAnnotationException(rootClass);
            }
            return Optional.of(Pair.of((Inject) annotationClass, configPropertyAnnotation.get().property()));
        }
        return Optional.empty();
    }

    private boolean satisfiesModifierConditions(final Field field) {
        final int fieldModifiers = field.getModifiers();
        final boolean requiredExist = this.modifiersRequiredToExist.stream().allMatch(i -> (fieldModifiers & i) != 0);
        final boolean requiredNotExist = this.modifiersRequiredToNotExist.stream().allMatch(i -> (fieldModifiers & i) == 0);
        return requiredExist && requiredNotExist;
    }

    <T> void setFieldWithValue(final Field field, final T value, final InjectionGroup binding_pair, final boolean optional) {
        if (!optional && value == null) {
            throw new NullObjectInjectionException(field);
        }

        if (!this.satisfiesModifierConditions(field)) {
            throw new FinalFieldInjectionException(field, value);
        }
        field.setAccessible(true);
        try {
            final Object newInstance = binding_pair.getRight().isPresent() ? binding_pair.getRight().get() : binding_pair.getLeft().newInstance();
            field.set(
                binding_pair.getLeft().cast(newInstance),
                value == null ? field.getType().newInstance() : field.getType().cast(value)
            );
        } catch (ClassCastException | InstantiationException e) {
            throw new FieldValueTypeCoercionException(value, field.getType());
        } catch (IllegalAccessException e) {
            throw new FinalFieldInjectionException(field, value);
        }
    }

    void saturateClassFields(final InjectionGroup binding_pair) {
        final Field[] fields = binding_pair.getLeft().getDeclaredFields();
        for (final Field field : fields) {
            Optional<Pair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, binding_pair.getLeft());
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final Pair<Inject, String> pair = hasAnnotation.get();
            final String configPropertyValue = pair.getRight();
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), binding_pair, pair.getLeft().optional());
        }
    }

    abstract BindingFactory requestInjection(final InjectionGroup ...bindings);

    abstract void build();

}
