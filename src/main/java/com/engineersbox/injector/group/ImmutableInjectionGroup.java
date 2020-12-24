package com.engineersbox.injector.group;

import java.util.Optional;

public final class ImmutableInjectionGroup extends InjectionGroup {

    public final Class<?> left;
    public final Optional<Object> right;

    public ImmutableInjectionGroup(final Class<?>  left, final Optional<Object> right) {
        this.left = left;
        this.right = right;
    }

    public static ImmutableInjectionGroup of(final Class<?> left, final Optional<Object> right) {
        return new ImmutableInjectionGroup(left, right);
    }

    public static ImmutableInjectionGroup empty() {
        return new ImmutableInjectionGroup(null, null);
    }

    @Override
    public Class<?> getLeft() {
        return this.left;
    }

    @Override
    public Optional<Object> getRight() {
        return this.right;
    }

}
