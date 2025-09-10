package me.kryptxn.cobraclient.valuesystem;

import java.util.ArrayList;

public class Value<T> {
    T value;
    String name;
    String owner;

    public Value(String name, String owner,T value) {
        this.name = name;
        this.owner = owner;
        this.value = value;

        register(this);
    }
    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    private static ArrayList<Value<?>> values = new ArrayList<>();;

    public static void register(Value<?> value) {
        values.add(value);
    }

    public static Value<?> getValue(String name, String owner) {
        for(Value<?> value : values) {
            if(value.getName().equalsIgnoreCase(name) && value.getOwner().equalsIgnoreCase(owner)) {
                return value;
            }
        }
        return null;
    }
}
