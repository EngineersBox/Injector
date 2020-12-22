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
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DynamicBindingFactory extends BindingFactory{

    private final Set<Pair<Class<?>, Optional<Object>>> requestedBindings;
    private ConfigurationProperties injectionSource;

    public DynamicBindingFactory() {
        this.requestedBindings = new HashSet<>();
        this.modifiersRequiredToExist = Collections.EMPTY_LIST;
        this.modifiersRequiredToNotExist = Collections.singletonList(Modifier.FINAL);
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
        this.requestedBindings.add(new Pair<>(class_to_bind, Optional.of(object_to_bind)));
        return this;
    }

    private void saturateClassFields(final Pair<Class<?>, Optional<Object>> binding_pair) {
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
        for (Pair<Class<?>, Optional<Object>> bindingPair : this.requestedBindings) {
            saturateClassFields(bindingPair);
        }
    }
}
