package com.engineersbox.injector.binding;

import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BindingFactoryTests {

    static class Field1 {
        @ConfigProperty(property = "field1")
        @Inject
        private static String field1;
        
        public String getField() {
            return field1;
        }
    }

    @Test
    public void canStaticInjectValidPropertyToField() {
        Field1 source = new Field1();
        new BindingFactory()
                .setInjectionSource("resources/configuration.properties")
                .requestStaticInjection(Field1.class)
                .build();
        Assertions.assertEquals(source.getField(), "field1");
    }

    static class Field2 {
        @ConfigProperty(property = "field2")
        @Inject(optional = true)
        private static String field2;

        public String getField() {
            return field2;
        }
    }

    @Test
    public void doesNotThrowWhenMissingPropertyWhenOptional() {
        Field2 source = new Field2();
        new BindingFactory()
                .setInjectionSource("resources/configuration.properties")
                .requestStaticInjection(Field2.class)
                .build();
        Assertions.assertNotNull(source.getField());
    }

    static class Field3 {
        @ConfigProperty(property = "field3")
        @Inject
        private static String field3;

        public String getField() {
            return field3;
        }
    }

    @Test
    public void throwsWhenMissingPropertyWhenNotOptional() {
        Field3 source = new Field3();
        Assertions.assertThrows(NullObjectInjectionException.class, () -> {
            new BindingFactory()
                    .setInjectionSource("resources/configuration.properties")
                    .requestStaticInjection(Field3.class)
                    .build();
        });
    }

    static class Field4 {
        @Inject
        private static String field4;

        public String getField() {
            return field4;
        }
    }

    @Test
    public void throwsWhenMissingPropertyAnnotationWithInjector() {
        Field4 source = new Field4();
        Assertions.assertThrows(MissingConfigPropertyAnnotationException.class, () -> {
            new BindingFactory()
                    .setInjectionSource("resources/configuration.properties")
                    .requestStaticInjection(Field4.class)
                    .build();
        });
    }

    static class Field5 {
        @ConfigProperty(property = "field5")
        @Inject
        private static final String field5 = "";

        public String getField() {
            return field5;
        }
    }

    @Test
    public void throwsWhenFieldIsFinal() {
        Field5 source = new Field5();
        Assertions.assertThrows(FinalFieldInjectionException.class, () -> {
            new BindingFactory()
                    .setInjectionSource("resources/configuration.properties")
                    .requestStaticInjection(Field5.class)
                    .build();
        });
    }
}
