package com.hooby.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class SimpleBeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObjects = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SimpleBeanFactory.class);

    public void registerBeanDefinition(BeanDefinition def) {
        beanDefinitionMap.put(def.getId(), def);
    }

    public Object getBean(String id) {
        if (singletonObjects.containsKey(id)) return singletonObjects.get(id);

        BeanDefinition def = beanDefinitionMap.get(id);
        if (def == null) throw new RuntimeException("❌ 등록되지 않은 빈입니다: " + id);

        try {
            Class<?> clazz = Class.forName(def.getClassName());
            logger.debug("✅ 클래스 로딩: {}", clazz.getName());

            Object instance;

            if (!def.getConstructorArgs().isEmpty()) {
                List<Object> args = new ArrayList<>();
                for (Object arg : def.getConstructorArgs()) {
                    if (arg instanceof String refId) {
                        args.add(getBean(refId));
                    } else if (arg instanceof List<?> listArg) {
                        List<Object> resolved = new ArrayList<>();
                        for (Object item : listArg) {
                            if (item instanceof String ref) resolved.add(getBean(ref));
                            else resolved.add(item); // 익명 객체
                        }
                        args.add(resolved);
                    } else if (arg instanceof Map<?, ?> mapArg) {
                        args.add(mapArg);
                    } else {
                        throw new RuntimeException("❌ 지원되지 않는 constructor-arg 타입: " + arg);
                    }
                }

                Constructor<?> matched = null;
                for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (paramTypes.length != args.size()) continue;

                    boolean match = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        Object actual = args.get(i);
                        Class<?> expected = paramTypes[i];
                        if (!expected.isAssignableFrom(actual.getClass())) {
                            if (!(expected == List.class && actual instanceof List) &&
                                    !(expected == Map.class && actual instanceof Map)) {
                                match = false;
                                break;
                            }
                        }
                    }
                    if (match) {
                        matched = ctor;
                        break;
                    }
                }

                if (matched == null) throw new RuntimeException("❌ 적절한 생성자를 찾을 수 없습니다: " + id);

                logger.debug("✅ 선택된 생성자: {}", matched);
                instance = matched.newInstance(args.toArray());
                logger.debug("✅ 인스턴스 생성 완료: {}", clazz.getName());
            } else {
                instance = clazz.getDeclaredConstructor().newInstance();
            }

            for (PropertyValue pv : def.getProperties()) {
                String name = pv.getName();
                Object ref = pv.getRef();

                Object value;
                if (ref instanceof String s) {
                    value = getBean(s);
                } else if (ref instanceof List<?> list) {
                    List<Object> resolved = new ArrayList<>();
                    for (Object item : list) {
                        if (item instanceof String refId) resolved.add(getBean(refId));
                        else resolved.add(item);
                    }
                    value = resolved;
                } else if (ref instanceof Map<?, ?>) {
                    value = ref;
                } else {
                    throw new RuntimeException("❌ 지원되지 않는 property 타입: " + ref);
                }

                String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
                Method setter = null;
                for (Method m : clazz.getMethods()) {
                    if (m.getName().equals(setterName) && m.getParameterCount() == 1) {
                        Class<?> param = m.getParameterTypes()[0];
                        if (param.isAssignableFrom(value.getClass()) ||
                                (param == List.class && value instanceof List) ||
                                (param == Map.class && value instanceof Map)) {
                            setter = m;
                            break;
                        }
                    }
                }
                if (setter == null) throw new RuntimeException("❌ setter 메서드를 찾을 수 없습니다: " + setterName);

                logger.debug("✅ setter 주입: {} → {}", setter.getName(), value.getClass().getName());
                setter.invoke(instance, value);
            }

            if (def.getInitMethod() != null) {
                Method init = clazz.getMethod(def.getInitMethod());
                logger.debug("✅ init-method 실행: {}", init.getName());
                init.invoke(instance);
            }

            singletonObjects.put(id, instance);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("❌ Bean 생성 실패: " + id + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", e);
        }
    }

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