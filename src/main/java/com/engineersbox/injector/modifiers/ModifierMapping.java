package com.engineersbox.injector.modifiers;

import java.lang.reflect.Modifier;

public enum ModifierMapping {
    ABSTRACT(Modifier.ABSTRACT),
    FINAL(Modifier.FINAL),
    INTERFACE(Modifier.INTERFACE),
    NATIVE(Modifier.NATIVE),
    PRIVATE(Modifier.PRIVATE),
    PROTECTED(Modifier.PROTECTED),
    PUBLIC(Modifier.PUBLIC),
    STATIC(Modifier.STATIC),
    STRICT(Modifier.STRICT),
    SYNCHRONIZED(Modifier.SYNCHRONIZED),
    TRANSIENT(Modifier.TRANSIENT),
    VOLATILE(Modifier.VOLATILE);

    private final int hexValue;

    ModifierMapping(final int hexValue) {
        this.hexValue = hexValue;
    }

    public int hexValue() {
        return this.hexValue;
    }
}
