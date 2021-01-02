package com.engineersbox.injector.modifiers;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    public static int toModifierHex(final ModifierMapping ...modifiers) {
        return Stream.of(modifiers)
            .mapToInt(ModifierMapping::hexValue)
            .reduce((a, b) -> a | b)
            .orElse(0x0);
    }

    public static List<ModifierMapping> toModifierList(final int modifiers) {
        return Stream.of(ModifierMapping.values())
            .filter(mapping -> (modifiers & mapping.hexValue()) != 0x0)
            .collect(Collectors.toList());
    }
}
