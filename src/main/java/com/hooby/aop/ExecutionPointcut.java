package com.hooby.aop;

import java.lang.reflect.Method;
import java.util.List;

public class ExecutionPointcut implements Pointcut {
    private final String classPattern;
    private final String methodPattern;
    private final String returnTypePattern;
    private final List<String> paramTypePatterns;

    public ExecutionPointcut(String classPattern, String methodPattern, String returnTypePattern, List<String> paramTypePatterns) {
        this.classPattern = classPattern;
        this.methodPattern = methodPattern;
        this.returnTypePattern = returnTypePattern;
        this.paramTypePatterns = paramTypePatterns;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        String className = targetClass.getName();
        String methodName = method.getName();
        String returnType = method.getReturnType().getSimpleName();

        // 클래스, 메서드, 리턴타입 정규표현식 기반 매칭
        if (!className.matches(toRegex(classPattern))) return false;
        if (!methodName.matches(toRegex(methodPattern))) return false;
        if (!returnType.matches(toRegex(returnTypePattern))) return false;

        // 파라미터 패턴 매칭
        Class<?>[] paramTypes = method.getParameterTypes();

        if (paramTypePatterns.size() == 1 && "..".equals(paramTypePatterns.get(0))) {
            return true; // any parameters
        }

        if (paramTypePatterns.size() != paramTypes.length) return false;

        for (int i = 0; i < paramTypes.length; i++) {
            String expected = paramTypePatterns.get(i);
            String actual = paramTypes[i].getSimpleName();
            if (!actual.matches(toRegex(expected))) return false;
        }

        return true;
    }

    private String toRegex(String pattern) {
        return pattern.replace(".", "\\.").replace("*", ".*");
    }
}