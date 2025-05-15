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
    private final List<BeanPostProcessor> postProcessors = new ArrayList<>();

    public void registerBeanDefinition(BeanDefinition def) {
        beanDefinitionMap.put(def.getId(), def);
    }

    @Override
    public Object getBean(String id) {
        if (singletonObjects.containsKey(id)) return singletonObjects.get(id); // 싱글톤 객체에 있으면 그냥 싱글벙글 쓰면 된다.
        BeanDefinition def = beanDefinitionMap.get(id);
        if (def == null) throw new RuntimeException("❌ 등록되지 않은 빈입니다: " + id);

        try {
            Class<?> clazz = Class.forName(def.getClassName()); // 내부적으로 Class Loader 를 사용해서 동적 클래스를 로딩한다.
            logger.debug("✅ 클래스 로딩: {}", clazz.getName());

            Object instance = createInstance(clazz, def);
            injectProperties(clazz, instance, def);
            invokeInitMethod(clazz, instance, def);

            // AOP Proxy 적용
            for (BeanPostProcessor processor : postProcessors) {
                instance = processor.postProcessAfterInitialization(instance);
            }

            singletonObjects.put(id, instance); // 싱글톤에 덮어씀 (원래 내부 로직을 분리를 할까도 했는데...)
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("❌ Bean 생성 실패: " + id + " (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")", e);
        }
    }

    private Object createInstance(Class<?> clazz, BeanDefinition def) throws Exception {
        /* Default Constructor Case */
        if (def.getConstructorArgs().isEmpty()) {
            return clazz.getDeclaredConstructor().newInstance();
        }

        /* Parameterized Constructor (DI Target) */

        // Define arguments list
        List<Object> args = new ArrayList<>();
        for (Object arg : def.getConstructorArgs()) {
            args.add(resolveValue(arg)); // resolveValue = return bean (∵ DI 대상이 객체)
        }

        // Matching Constructor
        Constructor<?> ctor = selectMatchingConstructor(clazz, args); // Matched Constructor
        if (ctor == null) throw new RuntimeException("❌ 적절한 생성자를 찾을 수 없습니다: " + def.getId());

        logger.debug("✅ 선택된 생성자: {}", ctor);
        Object instance = ctor.newInstance(args.toArray()); // Create Instance with args (∵ Parameterized Constructor)
        logger.debug("✅ 인스턴스 생성 완료: {}", clazz.getName());
        return instance;
    }

    private Constructor<?> selectMatchingConstructor(Class<?> clazz, List<Object> args) {
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) { // retrieve all constructors
            if (ctor.getParameterCount() != args.size()) continue; // Matching DefinitionBean info

            boolean match = true; // Default Setting
            Class<?>[] paramTypes = ctor.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                Object actual = args.get(i); // args are beans
                if (!paramTypes[i].isAssignableFrom(actual.getClass())) { // isNotAssignable? -> mismatch
                    match = false;
                    break;
                }
            }
            if (match) return ctor; // return Matched Constructor
        }
        return null;
    }

    private void injectProperties(Class<?> clazz, Object instance, BeanDefinition def) throws Exception {
        for (PropertyValue pv : def.getProperties()) {
            // Pattern Matching to find setters
            String setterName = "set" + Character.toUpperCase(pv.getName().charAt(0)) + pv.getName().substring(1);

            // value : The target instance to inject
            Object value = resolveValue(pv.getRef());

            Method setter = null;
            for (Method method : clazz.getMethods()) { // retrieve all methods
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) { // Matching setter method
                    Class<?> paramType = method.getParameterTypes()[0]; // setter parameter type
                    if (paramType.isAssignableFrom(value.getClass()) || // isAssignable?
                            (paramType == List.class && value instanceof List) || // beans.xml 에서 객체 여러 개를 list 로 묶은 경우
                            (paramType == Map.class && value instanceof Map)) { // beans.xml 에서 객체를 map 으로 묶은 경우 (Servlet Mapper)
                        setter = method; // confirm setter
                        break;
                    }
                }
            }

            if (setter == null) throw new RuntimeException("❌ setter 메서드를 찾을 수 없습니다: " + setterName);
            logger.debug("✅ setter 주입: {} → {}", setter.getName(), value.getClass().getName());
            setter.invoke(instance, value); // run setter method dynamically
        }
    }

    private void invokeInitMethod(Class<?> clazz, Object instance, BeanDefinition def) throws Exception {
        if (def.getInitMethod() != null) { // If InitMethod is Exist
            Method init = clazz.getMethod(def.getInitMethod()); // retrieve init method
            logger.debug("✅ init-method 실행: {}", init.getName());
            init.invoke(instance); // run init method dynamically for managing life cycle
        }
    }

    private Object resolveValue(Object ref) {
        // Selective Recursion Case : s (=ref) : Bean ID Case -> 해당 ID 에 대한 Bean 생성 후 반환
        if (ref instanceof String s) return getBean(s);
        if (ref instanceof List<?> list) {
            List<Object> resolved = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof String refId) resolved.add(getBean(refId));
                else resolved.add(item); // already resolved value
            }
            return resolved;
        }
        return ref; // already resolved value (Terminate condition of Recursion)
    }

    @Override
    public void close() {
        for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) { // retrieve all singleton beans
            BeanDefinition def = beanDefinitionMap.get(entry.getKey()); // bean key 로 DefinitionMap 에서 찾아서 def 정의
            if (def.getDestroyMethod() != null) { // If Destroy Method is Exist (case : 메타 데이터 상에 있었음? -> yes)
                try {
                    Method destroy = entry.getValue().getClass().getMethod(def.getDestroyMethod());
                    destroy.invoke(entry.getValue()); // run destroy method for managing lifecycle
                } catch (Exception e) {
                    System.err.println("❌ destroy-method 실행 실패: " + def.getId());
                }
            }
        }
    }

    public void addPostProcessor(BeanPostProcessor processor) {
        postProcessors.add(processor);
    }
}