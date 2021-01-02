# Injector

![Injector Build Status](https://img.shields.io/github/workflow/status/EngineersBox/Injector/Java%20CI%20with%20Maven?style=for-the-badge)

A Java dependency injection library supporting both static and dynamic injection

## Static Field Injection

Properties file:
```properties
field1=some injection content
field2=other content
```

Injection code:

```java
public class Main {
    class InjectTo {
        @ConfigProperty(property = "field1")
        @Inject
        private static String field1;

        @ConfigProperty
        @Inject
        private static String field2;

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
        }
    }

    public static void main() {
        InjectTo source = new InjectTo();
        new StaticBindingFactory()
                .setInjectionSource("resources/configuration.properties")
                .requestInjection(InjectionGroup.of(InjectTo.class))
                .build();

        // Prints: some injection content
        System.out.println(source.getField1());
        
        // Prints: other content
        System.out.println(source.getField2());
    }
}
```

## Dynamic Field Injection

Properties file:
```properties
field1=some injection content
field2=other content
```

Injection code:
```java
public class Main {
    class InjectTo {
        @ConfigProperty(property = "field1")
        @Inject
        private String field1;

        @ConfigProperty
        @Inject
        private String field2;

        public String getField1() {
            return field1;
        }

        public String getField2() {
            return field2;
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
        System.out.println(source.getField1());

        // Prints: other content
        System.out.println(source.getField2());
        
        // Prints: null
        System.out.println(otherSource.getField1());

        // Prints: null
        System.out.println(otherSource.getField2());
    }
}
```

## Constructor Injection

Properties file:
```properties
config_string=some config string
```

Injection code:

```java
public class TextEditor {
    private SpellChecker spellChecker;
    private String configString;

    @Inject
    public TextEditor(@ConfigProperty(property="config_string") String configString, SpellChecker spellChecker) {
        this.configString = configString;
        this.spellChecker = spellChecker;
    }

    public String makeSpellCheck() {
        return spellChecker.checkSpelling();
    }

    public String getConfigString() {
        return configString;
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
        Injector injector = Injector.createInjector(new TextEditorModule())
                .setInjectionSource("resources/configuration.properties");
        TextEditor editor = injector.getInstance(TextEditor.class);

        // Prints: Called checkSpelling()
        System.out.println(editor.makeSpellCheck());
        
        // Prints: some config string
        System.out.println(editor.getConfigString());
    }
}
```

## Method Injection

```Java
class TextEditor {
   private SpellChecker spellChecker;

   @Inject
   public TextEditor( SpellChecker spellChecker) {
      this.spellChecker = spellChecker;
   }

   public void makeSpellCheck(){
      spellChecker.checkSpelling();
   } 
}

class TextEditorModule extends AbstractModule {

   @Override
   public void configure() { 
      bind(String.class)
         .annotatedWith(Names.named("JDBC"))
         .toInstance("jdbc:mysql://localhost:5326/emp");
   } 
}

@ImplementedBy(SpellCheckerImpl.class)
interface SpellChecker {
   public void checkSpelling();
}

class SpellCheckerImpl implements SpellChecker {
 
   private String dbUrl;

   public SpellCheckerImpl(){}
   
   @Inject 
   public void setDbUrl(@Named("JDBC") String dbUrl){
      this.dbUrl = dbUrl;
   }

   @Override
   public void checkSpelling() { 
       return "Called checkSpelling() with URL: " + dbUrl;
   }
}

public class Main {
    public static void main() {
        Injector injector = Injector.createInjector(new TextEditorModule());
        TextEditor editor = injector.getInstance(TextEditor.class);

        // Prints: Called checkSpelling() with URL: jdbc:mysql://localhost:5326/emp
        System.out.println(editor.makeSpellCheck());
    }
}
```