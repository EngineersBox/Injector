package com.engineersbox.injector.binding;

import com.engineersbox.injector.group.InjectionGroup;
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
                .requestInjection(InjectionGroup.of(Field1.class, source))
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
    public void canDynamicInjectMultipleValidPropertyToField() {
        Field1 source = new Field1();
        Field1 source2 = new Field1();
        Field2 source3 = new Field2();
        new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(
                    InjectionGroup.of(Field1.class, source),
                    InjectionGroup.of(Field1.class, source2),
                    InjectionGroup.of(Field2.class, source3)
                )
                .build();
        Assertions.assertEquals(source.getField(), "field1");
        Assertions.assertEquals(source2.getField(), "field1");
        Assertions.assertEquals(source3.getField(), "field2");
    }

    static class Field3 {
        @ConfigProperty(property = "field2")
        @Inject(optional = true)
        private String field3;

        public String getField() {
            return field3;
        }
    }

    @Test
    public void doesNotThrowWhenMissingPropertyWhenOptional() {
        Field3 source = new Field3();
        Field3 source2 = new Field3();
        new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(InjectionGroup.of(Field3.class, source))
                .build();
        Assertions.assertNotNull(source.getField());
        Assertions.assertNull(source2.getField());
    }

    static class Field4 {
        @ConfigProperty(property = "field3")
        @Inject
        private String field4;
    }

    @Test
    public void throwsWhenMissingPropertyWhenNotOptional() {
        Field4 source = new Field4();
        Assertions.assertThrows(NullObjectInjectionException.class, () -> new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(InjectionGroup.of(Field4.class, source))
                .build());
    }

    static class Field5 {
        @Inject
        private String field5;
    }

    @Test
    public void throwsWhenMissingPropertyAnnotationWithInjector() {
        Field5 source = new Field5();
        Assertions.assertThrows(MissingConfigPropertyAnnotationException.class, () -> new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(InjectionGroup.of(Field5.class, source))
                .build());
    }

    static class Field6 {
        @ConfigProperty(property = "field6")
        @Inject
        private final String field6 = "";

        public String getField() {
            return field6;
        }
    }

    @Test
    public void throwsWhenFieldIsFinal() {
        Field6 source = new Field6();
        Assertions.assertThrows(FinalFieldInjectionException.class, () -> new DynamicBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(InjectionGroup.of(Field6.class, source))
                .build());
        Assertions.assertEquals(source.getField(), "");
    }

    static class Field7 {
        @Inject(optional = true)
        private static String field7 = null;

        public String getField() {
            return field7;
        }
    }

    @Test
    public void injectsDefaultConstructedObjectWhenMissingConfigPropertyAndIsOptional() throws IllegalAccessException, InstantiationException {
        StaticBindingFactoryTests.Field7 source = new StaticBindingFactoryTests.Field7();
        new StaticBindingFactory()
                .setInjectionSource("resources/dynamictests.properties")
                .requestInjection(InjectionGroup.of(StaticBindingFactoryTests.Field7.class))
                .build();
        Assertions.assertNotNull(source.getField());
        Assertions.assertEquals(source.getField(), String.class.newInstance());
    }
}
