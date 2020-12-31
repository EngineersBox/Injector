package com.engineersbox.injector.binding;

import com.engineersbox.injector.ConfigurationProperties;
import com.engineersbox.injector.annotations.AnnotationUtils;
import com.engineersbox.injector.group.InjectionGroup;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FieldValueTypeCoercionException;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;
import com.engineersbox.injector.modifiers.ModifierRequirement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public abstract class BindingFactory {

    ModifierRequirement modifierRequirement;
    ConfigurationProperties injectionSource;

    abstract BindingFactory setInjectionSource(final String filename);

    Optional<Pair<Inject, String>> getInjectorAnnotations(final Field field, final Class<?> rootClass) {
        for (final Annotation annotationClass : field.getDeclaredAnnotations()) {
            final Class<? extends Annotation> annotationType = annotationClass.annotationType();
            if (!annotationType.equals(Inject.class)) {
                continue;
            }
            final Inject injectAnnotation = (Inject) annotationClass;
            final Optional<? extends Annotation> configPropertyAnnotation = AnnotationUtils.getAnnotation(field, ConfigProperty.class);
            if (!injectAnnotation.optional() && !configPropertyAnnotation.isPresent()) {
                throw new MissingConfigPropertyAnnotationException(rootClass);
            }
            String propertyValue = null;
            if (configPropertyAnnotation.isPresent()) {
                final ConfigProperty configProperty = (ConfigProperty) configPropertyAnnotation.get();
                propertyValue = configProperty.property();
            }
            return Optional.of(Pair.of(injectAnnotation, propertyValue));
        }
        return Optional.empty();
    }

    <T> void setFieldWithValue(final Field field, final T value, final InjectionGroup binding_pair, final boolean optional) {
        if (!optional && value == null) {
            throw new NullObjectInjectionException(field);
        }

        if (!this.modifierRequirement.assertModifierCombination(field.getModifiers())) {
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
            final String configPropertyValue = StringUtils.isEmpty(pair.getRight()) ? field.getName() : pair.getRight();
            setFieldWithValue(field, configPropertyValue == null ? null : this.injectionSource.properties.getProperty(configPropertyValue), binding_pair, pair.getLeft().optional());
        }
    }

    abstract BindingFactory requestInjection(final InjectionGroup ...bindings);

    abstract void build();

}
