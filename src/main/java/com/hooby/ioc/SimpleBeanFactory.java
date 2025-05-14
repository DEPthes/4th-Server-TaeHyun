package com.hooby.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class SimpleBeanFactory implements BeanFactory {

    protected final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    protected final Map<String, Object> singletonObjects = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SimpleBeanFactory.class);

    public void registerBeanDefinition(BeanDefinition def) {
        beanDefinitionMap.put(def.getId(), def);
    }

    @Override
    public Object getBean(String id) {
        if (singletonObjects.containsKey(id)) return singletonObjects.get(id);
        BeanDefinition def = beanDefinitionMap.get(id);
        if (def == null) throw new RuntimeException("❌ 등록되지 않은 빈입니다: " + id);

        try {
            Class<?> clazz = Class.forName(def.getClassName());
            logger.debug("✅ 클래스 로딩: {}", clazz.getName());

            Object instance = createInstance(clazz, def);
            injectProperties(clazz, instance, def);
            invokeInitMethod(clazz, instance, def);

            singletonObjects.put(id, instance);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("❌ Bean 생성 실패: " + id + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", e);
        }
    }

    private Object createInstance(Class<?> clazz, BeanDefinition def) throws Exception {
        if (def.getConstructorArgs().isEmpty()) {
            return clazz.getDeclaredConstructor().newInstance();
        }

        List<Object> args = new ArrayList<>();
        for (Object arg : def.getConstructorArgs()) {
            args.add(resolveValue(arg));
        }

        Constructor<?> ctor = selectMatchingConstructor(clazz, args);
        if (ctor == null) throw new RuntimeException("❌ 적절한 생성자를 찾을 수 없습니다: " + def.getId());

        logger.debug("✅ 선택된 생성자: {}", ctor);
        Object instance = ctor.newInstance(args.toArray());
        logger.debug("✅ 인스턴스 생성 완료: {}", clazz.getName());
        return instance;
    }

    private Constructor<?> selectMatchingConstructor(Class<?> clazz, List<Object> args) {
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.getParameterCount() != args.size()) continue;

            boolean match = true;
            Class<?>[] paramTypes = ctor.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Object actual = args.get(i);
                if (!paramTypes[i].isAssignableFrom(actual.getClass())) {
                    match = false;
                    break;
                }
            }
            if (match) return ctor;
        }
        return null;
    }

    private void injectProperties(Class<?> clazz, Object instance, BeanDefinition def) throws Exception {
        for (PropertyValue pv : def.getProperties()) {
            String setterName = "set" + Character.toUpperCase(pv.getName().charAt(0)) + pv.getName().substring(1);
            Object value = resolveValue(pv.getRef());

            Method setter = null;
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    if (paramType.isAssignableFrom(value.getClass()) ||
                            (paramType == List.class && value instanceof List) ||
                            (paramType == Map.class && value instanceof Map)) {
                        setter = method;
                        break;
                    }
                }
            }

            if (setter == null) throw new RuntimeException("❌ setter 메서드를 찾을 수 없습니다: " + setterName);
            logger.debug("✅ setter 주입: {} → {}", setter.getName(), value.getClass().getName());
            setter.invoke(instance, value);
        }
    }

    private void invokeInitMethod(Class<?> clazz, Object instance, BeanDefinition def) throws Exception {
        if (def.getInitMethod() != null) {
            Method init = clazz.getMethod(def.getInitMethod());
            logger.debug("✅ init-method 실행: {}", init.getName());
            init.invoke(instance);
        }
    }

    private Object resolveValue(Object ref) {
        if (ref instanceof String s) return getBean(s);
        if (ref instanceof List<?> list) {
            List<Object> resolved = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof String refId) resolved.add(getBean(refId));
                else resolved.add(item);
            }
            return resolved;
        }
        return ref;
    }

    @Override
    public void close() {
        for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
            BeanDefinition def = beanDefinitionMap.get(entry.getKey());
            if (def.getDestroyMethod() != null) {
                try {
                    Method destroy = entry.getValue().getClass().getMethod(def.getDestroyMethod());
                    destroy.invoke(entry.getValue());
                } catch (Exception e) {
                    System.err.println("❌ destroy-method 실행 실패: " + def.getId());
                }
            }
        }
    }
}