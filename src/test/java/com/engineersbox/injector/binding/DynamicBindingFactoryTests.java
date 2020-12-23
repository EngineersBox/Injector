package com.engineersbox.injector.binding;

import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DynamicBindingFactoryTests {

    static class Field1 {
        @ConfigProperty(property = "field1")
        @Inject
        private String field1;
        
        public String getField() {
            return field1;
        }
    }

    @Test
    public void canDynamicInjectValidPropertyToField() {
        Field1 source = new Field1();
        Field1 source2 = new Field1();
        new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(Field1.class, source)
                .build();
        Assertions.assertEquals(source.getField(), "field1");
        Assertions.assertNull(source2.getField());
    }

    static class Field2 {
        @ConfigProperty(property = "field2")
        @Inject(optional = true)
        private String field2;

        public String getField() {
            return field2;
        }
    }

    @Test
    public void doesNotThrowWhenMissingPropertyWhenOptional() {
        Field2 source = new Field2();
        Field2 source2 = new Field2();
        new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(Field2.class, source)
                .build();
        Assertions.assertNotNull(source.getField());
        Assertions.assertNull(source2.getField());
    }

    static class Field3 {
        @ConfigProperty(property = "field3")
        @Inject
        private String field3;
    }

    @Test
    public void throwsWhenMissingPropertyWhenNotOptional() {
        Field3 source = new Field3();
        Assertions.assertThrows(NullObjectInjectionException.class, () -> {
            new DynamicBindingFactory()
                    .setInjectionSource("resources/dynamictests.properties")
                    .requestInjection(Field3.class, source)
                    .build();
        });
    }

    static class Field4 {
        @Inject
        private String field4;
    }

    @Test
    public void throwsWhenMissingPropertyAnnotationWithInjector() {
        Field4 source = new Field4();
        Assertions.assertThrows(MissingConfigPropertyAnnotationException.class, () -> {
            new DynamicBindingFactory()
                    .setInjectionSource("resources/dynamictests.properties")
                    .requestInjection(Field4.class, source)
                    .build();
        });
    }

    static class Field5 {
        @ConfigProperty(property = "field5")
        @Inject
        private final String field5 = "";

        public String getField() {
            return field5;
        }
    }

    @Test
    public void throwsWhenFieldIsFinal() {
        Field5 source = new Field5();
        Assertions.assertThrows(FinalFieldInjectionException.class, () -> {
            new DynamicBindingFactory()
                    .setInjectionSource("resources/dynamictests.properties")
                    .requestInjection(Field5.class, source)
                    .build();
        });
        Assertions.assertEquals(source.getField(), "");
    }
}
