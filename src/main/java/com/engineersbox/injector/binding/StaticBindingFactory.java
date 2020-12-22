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
import java.lang.reflect.Modifier;
import java.util.*;

public class StaticBindingFactory extends BindingFactory {

    private final Set<Class<?>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public StaticBindingFactory(){
        this.requestedBindings = new HashSet<>();
        this.modifiersRequiredToExist = Collections.singletonList(Modifier.STATIC);
        this.modifiersRequiredToNotExist = Collections.singletonList(Modifier.FINAL);
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

    private void saturateClassFields(final Class<?> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Optional<Pair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, clazz);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final Pair<Inject, String> pair = hasAnnotation.get();
            final String configPropertyValue = pair.right;
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), new Pair<>(clazz, Optional.empty()), pair.left.optional());
        }
    }

    @Override
    public void build() {
        for (Class<?> clazz : this.requestedBindings) {
            saturateClassFields(clazz);
        }
    }

}
