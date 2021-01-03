package com.engineersbox.injector.method;

import com.engineersbox.injector.annotations.Inject;

public class TextEditor {
    private SpellChecker spellChecker;

    @Inject
    public TextEditor(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    public String makeSpellCheck(){
        return spellChecker.checkSpelling();
    }
}
