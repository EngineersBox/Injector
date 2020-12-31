package com.engineersbox.injector.binding;

import com.engineersbox.injector.group.InjectionGroup;
import com.engineersbox.injector.annotations.ConfigProperty;
import com.engineersbox.injector.annotations.Inject;
import com.engineersbox.injector.exceptions.FinalFieldInjectionException;
import com.engineersbox.injector.exceptions.MissingConfigPropertyAnnotationException;
import com.engineersbox.injector.exceptions.NullObjectInjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StaticBindingFactoryTests {

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
        new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field1.class))
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
    public void canStaticInjectMultipleValidPropertyToField() {
        Field1 source = new Field1();
        Field2 source2 = new Field2();
        new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(
                    InjectionGroup.of(Field1.class),
                    InjectionGroup.of(Field2.class)
                )
                .build();
        Assertions.assertEquals(source.getField(), "field1");
        Assertions.assertEquals(source2.getField(), "field2");
    }

    static class Field3 {
        @ConfigProperty(property = "field3")
        @Inject(optional = true)
        private static String field3;

        public String getField() {
            return field3;
        }
    }

    @Test
    public void doesNotThrowWhenMissingPropertyWhenOptional() {
        Field3 source = new Field3();
        new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field3.class))
                .build();
        Assertions.assertNotNull(source.getField());
    }

    static class Field4 {
        @ConfigProperty(property = "field3")
        @Inject
        private static String field4;
    }

    @Test
    public void throwsWhenMissingPropertyWhenNotOptional() {
        Field4 source = new Field4();
        Assertions.assertThrows(NullObjectInjectionException.class, () -> new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field4.class))
                .build());
    }

    static class Field5 {
        @Inject
        private static String field5;
    }

    @Test
    public void throwsWhenMissingPropertyAnnotationWithInjector() {
        Field5 source = new Field5();
        Assertions.assertThrows(MissingConfigPropertyAnnotationException.class, () -> new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field5.class))
                .build());
    }

    static class Field6 {
        @ConfigProperty(property = "field6")
        @Inject
        private static final String field6 = "";

        public String getField() {
            return field6;
        }
    }

    @Test
    public void throwsWhenFieldIsFinal() {
        Field6 source = new Field6();
        Assertions.assertThrows(FinalFieldInjectionException.class, () -> new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field6.class))
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
        Field7 source = new Field7();
        new StaticBindingFactory()
            .setInjectionSource("resources/statictests.properties")
            .requestInjection(InjectionGroup.of(Field7.class))
            .build();
        Assertions.assertNotNull(source.getField());
        Assertions.assertEquals(source.getField(), String.class.newInstance());
    }

    static class Field8 {
        @ConfigProperty
        @Inject
        private static String field8 = null;

        public String getField() {
            return field8;
        }
    }

    @Test
    public void injectsValueBasedOnFieldNameWhenHasConfigPropertyWithNoFieldSpecified() {
        Field8 source = new Field8();
        new StaticBindingFactory()
                .setInjectionSource("resources/statictests.properties")
                .requestInjection(InjectionGroup.of(Field8.class))
                .build();
        Assertions.assertEquals(source.getField(), "field8");
    }
}
