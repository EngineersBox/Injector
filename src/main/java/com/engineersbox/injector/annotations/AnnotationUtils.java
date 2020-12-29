package com.engineersbox.injector.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class AnnotationUtils {

    public static boolean hasAnnotation(final AnnotatedElement annotated_element, final Class<? extends Annotation> target_annotation) {
        for (final Annotation annotation : annotated_element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (!annotationType.equals(target_annotation)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static Optional<? extends Annotation> getAnnotation(final AnnotatedElement annotated_element, final Class<? extends Annotation> target_annotation) {
        for (final Annotation annotation : annotated_element.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.equals(target_annotation)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }
}
