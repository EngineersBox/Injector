package com.engineersbox.injector.constructor;

import com.engineersbox.injector.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConstructorInjectionTests {

    @Test
    public void canInjectWithConstructedParams() {
        Injector injector = Injector.createInjector(new TextEditorModule());
        TextEditor editor = injector.getInstance(TextEditor.class);
        Assertions.assertEquals(editor.makeSpellCheck(), "Called checkSpelling() method");
    }

    @Test
    public void canInjectWithConfigPropertyParams() {
        Injector injector = Injector.createInjector(new TextEditorModule()).setInjectionSource("resources/constructortests.properties");
        TextEditor2 editor = injector.getInstance(TextEditor2.class);
        Assertions.assertEquals(editor.getConfigString(), "some config string");
        Assertions.assertEquals(editor.makeSpellCheck(), "Called checkSpelling() method");
    }

}
