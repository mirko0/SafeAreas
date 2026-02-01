package com.mcodelogic.safeareas.model;

public final class FlagValue<T> {

    private final T value;
    private final boolean explicitlySet;

    public FlagValue(T value, boolean explicitlySet) {
        this.value = value;
        this.explicitlySet = explicitlySet;
    }

    public T getValue() {
        return value;
    }

    public boolean isExplicitlySet() {
        return explicitlySet;
    }
}
