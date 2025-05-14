package com.hooby.ioc;

public class PropertyValue {
    private final String name;
    private final Object ref;

    public PropertyValue(String name, Object ref) {
        this.name = name;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public Object getRef() { // ✅ 반환 타입도 Object
        return ref;
    }
}