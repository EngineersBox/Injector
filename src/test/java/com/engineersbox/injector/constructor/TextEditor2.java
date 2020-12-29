package com.engineersbox.injector.constructor;

import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;

public class TextEditor2 {
    private SpellChecker spellChecker;
    private String configString;

    @Inject
    public TextEditor2(@ConfigProperty(property = "config_string") String configString, SpellChecker spellChecker) {
        this.configString = configString;
        this.spellChecker = spellChecker;
    }

    public String makeSpellCheck(){
        return spellChecker.checkSpelling();
    }

    public String getConfigString() {
        return this.configString;
    }
}
