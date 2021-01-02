package com.engineersbox.injector.method;

import com.engineersbox.injector.Injector;
import org.junit.jupiter.api.Test;

public class MethodInjectionTests {

    @Test
    public void canInjectMethodWithValidInjector() {
        Injector injector = Injector.createInjector(new TextEditorModule());
        TextEditor editor = injector.getInstance(TextEditor.class);
        editor.makeSpellCheck();
    }

}
