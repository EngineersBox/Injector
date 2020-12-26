package com.engineersbox.injector.constructor;

import com.engineersbox.injector.module.AbstractModule;

public class TextEditorModule extends AbstractModule {
    @Override
    public void configure() {
        bind(SpellChecker.class).to(SpellCheckerImpl.class);
    }
}
