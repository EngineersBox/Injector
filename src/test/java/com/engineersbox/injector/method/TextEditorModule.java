package com.engineersbox.injector.method;

import com.engineersbox.injector.module.AbstractModule;
import com.engineersbox.injector.naming.Names;

public class TextEditorModule extends AbstractModule {

    @Override
    public void configure() {
        bind(String.class)
                .annotatedWith(Names.named("JDBC"))
                .toInstance("jdbc:mysql://localhost:5326/emp");
    }
}
