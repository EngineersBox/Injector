package com.engineersbox.injector.binding;

public class AnnotationBindingPair<K, V> {
    public K left;
    public V right;

    public AnnotationBindingPair(final K left, final V right) {
        this.left = left;
        this.right = right;
    }
}
