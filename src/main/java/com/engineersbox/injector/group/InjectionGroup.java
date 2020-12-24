package com.engineersbox.injector.group;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public abstract class InjectionGroup extends Pair<Class<?>, Optional<Object>> {

    private Class<?> left;
    private Optional<Object> right;

    public static InjectionGroup of(final Class<?> left) {
        return ImmutableInjectionGroup.of(left, Optional.empty());
    }

    public static InjectionGroup of(final Class<?> left, final Object right) {
        return ImmutableInjectionGroup.of(left, Optional.of(right));
    }

    public static InjectionGroup empty() {
        return ImmutableInjectionGroup.empty();
    }

    @Override
    public Class<?> getLeft() {
        return this.left;
    }

    @Override
    public Optional<Object> getRight() {
        return this.right;
    }

    @Override
    public Optional<Object> setValue(final Optional<Object> value) {
        final Optional<Object> previous = this.right;
        this.right = value;
        return previous;
    }
}
