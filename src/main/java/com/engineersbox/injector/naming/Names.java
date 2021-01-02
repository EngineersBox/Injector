package com.engineersbox.injector.naming;

import com.engineersbox.injector.annotations.Named;

public class Names {

    public static Named named(final String name) {
        return new ImmutableNamed(name);
    }

}
