package com.engineersbox.injector.binding;

import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.ConfigurationProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileNotFoundException;
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
        for (final Field field : fields) {
            Optional<Pair<Inject, String>> hasAnnotation = getInjectorAnnotations(field, clazz);
            if (!hasAnnotation.isPresent()) {
                continue;
            }
            final Pair<Inject, String> pair = hasAnnotation.get();
            final String configPropertyValue = pair.getRight();
            setFieldWithValue(field, this.injectionSource.properties.getProperty(configPropertyValue), Pair.of(clazz, Optional.empty()), pair.getLeft().optional());
        }
    }

    @Override
    public void build() {
        for (final Class<?> clazz : this.requestedBindings) {
            saturateClassFields(clazz);
        }
    }

}
