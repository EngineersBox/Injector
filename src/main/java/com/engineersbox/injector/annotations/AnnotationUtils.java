package com.engineersbox.injector.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationUtils {

    public static boolean hasAnnotation(final AnnotatedElement annotated_element, final Class<? extends Annotation> target_annotation) {
        return Stream.of(annotated_element.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().equals(target_annotation));
    }

    public static Optional<? extends Annotation> getAnnotation(final AnnotatedElement annotated_element, final Class<? extends Annotation> target_annotation) {
        return Stream.of(annotated_element.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(target_annotation))
                .findFirst();
    }
}
