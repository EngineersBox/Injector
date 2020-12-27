package com.engineersbox.injector.constructor;

import com.engineersbox.injector.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructorInjectionTests {

    @Test
    public void canInjectWithValidParams() {
        Injector injector = Injector.createInjector(new TextEditorModule());
        TextEditor editor = injector.getInstance(TextEditor.class);
        Assertions.assertEquals(editor.makeSpellCheck(), "Called checkSpelling() method");
    }

}
