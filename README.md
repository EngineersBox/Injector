# Injector

![Injector Build Status](https://img.shields.io/github/workflow/status/EngineersBox/Injector/Java%20CI%20with%20Maven?style=for-the-badge)

A Java dependency injection library supporting both static and dynamic injection

## Static Field Injection

Properties file:
```properties
field1=some injection content
```

Injection code:
```java
public class Main {
    class InjectTo {
        @ConfigProperty(property = "field1")
        @Inject
        private static String field1;

        public String getField() {
            return field1;
        }
    }
    
    public static void main() {
        InjectTo source = new InjectTo();
        new StaticBindingFactory()
                .setInjectionSource("resources/configuration.properties")
                .requestInjection(InjectionGroup.of(InjectTo.class))
                .build();
        
        // Prints: some injection content
        System.out.println(source.getField());
    }
}
```

## Dynamic Field Injection

Properties file:
```properties
field1=some injection content
```

Injection code:
```java
public class Main {
    class InjectTo {
        @ConfigProperty(property = "field1")
        @Inject
        private String field1;

        public String getField() {
            return field1;
        }
    }
    
    public static void main() {
        InjectTo source = new InjectTo();
        InjectTo otherSource = new InjectTo();
        new DynamicBindingFactory()
                .setInjectionSource("resources/configuration.properties")
                .requestInjection(InjectionGroup.of(InjectTo.class, source))
                .build();
        
        // Prints: some injection content
        System.out.println(source.getField());
        
        // Prints: null
        System.out.println(otherSource.getField());
    }
}
```

## Constructor Injection

```java
public class TextEditor {
    private SpellChecker spellChecker;

    @Inject
    public TextEditor(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    public String makeSpellCheck() {
        return spellChecker.checkSpelling();
    }
}

public class TextEditorModule extends AbstractModule {

    @Override
    public void configure() {
        bind(SpellChecker.class).to(SpellCheckerImpl.class);
    }
}

public interface SpellChecker {
    void checkSpelling();
}

public class SpellCheckerImpl implements SpellChecker {

    @Override
    public void checkSpelling() {
        return "Called checkSpelling()";
    }
}

public class Main {
   public static void main() {
      Injector injector = Injector.createInjector(new TextEditorModule());
      TextEditor editor = injector.getInstance(TextEditor.class);
      
      // Prints: Called checkSpelling()
      System.out.println(editor.makeSpellCheck());
   } 
}
```