package com.hooby.ioc;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {
    private final String id;
    private final String className;
    private final String initMethod;
    private final String destroyMethod;

    private final List<Object> constructorArgs = new ArrayList<>();
    private final List<PropertyValue> properties = new ArrayList<>();

    public BeanDefinition(String id, String className, String initMethod, String destroyMethod) {
        this.id = id;
        this.className = className;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    public String getId() { return id; }
    public String getClassName() { return className; }
    public String getInitMethod() { return initMethod; }
    public String getDestroyMethod() { return destroyMethod; }

    public void addConstructorArg(Object arg) {
        constructorArgs.add(arg);
    }

    public List<Object> getConstructorArgs() {
        return constructorArgs;
    }

    public void addProperty(PropertyValue property) {
        properties.add(property);
    }

    public List<PropertyValue> getProperties() {
        return properties;
    }
}