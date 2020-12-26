package com.engineersbox.injector.constructor;

import com.engineersbox.injector.Injector;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;


public class ConstructorInjectionTests {

    @Test
    public void canInjectWithValidParams() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Injector injector = Injector.createInjector(new TextEditorModule());
        TextEditor editor = injector.getInstance(TextEditor.class);
        editor.makeSpellCheck();
    }

}
