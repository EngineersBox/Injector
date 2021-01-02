package com.engineersbox.injector.naming;

import com.engineersbox.injector.annotations.Named;

import java.lang.annotation.Annotation;

public class NamedInst implements Named {

    private final String name;

    public NamedInst(final String name) {
        this.name = name;
    }

    @Override
    public String value() {
        return name;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }
}
