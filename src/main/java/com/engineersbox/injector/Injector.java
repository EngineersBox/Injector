package com.engineersbox.injector;

import com.engineersbox.injector.module.AbstractModule;
import com.engineersbox.injector.module.ModuleBinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Injector {

    private final AbstractModule module;

    private Injector(final AbstractModule module) {
        this.module = module;
    }

    public static Injector createInjector(final AbstractModule module) {
        return new Injector(module);
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(final Class<T> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        this.module.configure();
        final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final List<Object> parameters = new ArrayList<>();
        for (Class<?> paramClass : parameterTypes) {
            final Optional<ModuleBinding> moduleBinding = this.module.getModuleBindingForBindingClass(paramClass);
            if (moduleBinding.isPresent()) {
                parameters.add(moduleBinding.get().boundTo.newInstance());
                continue;
            }
            parameters.add(null);
        }
        return (T) constructor.newInstance(parameters);
    }
}
