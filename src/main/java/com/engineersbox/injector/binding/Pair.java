package com.engineersbox.injector.binding;

public class Pair<K, V> {
    public K left;
    public V right;

    public Pair(final K left, final V right) {
        this.left = left;
        this.right = right;
    }
}
