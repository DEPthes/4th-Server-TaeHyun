package com.hooby.aop;

import java.util.List;

public class ExecutionPointcutParser {
    public static ExecutionPointcut parse(String expression) {
        if (!expression.startsWith("execution(") || !expression.endsWith(")")) {
            throw new IllegalArgumentException("Invalid expression format");
        }

        String body = expression.substring("execution(".length(), expression.length() - 1).trim();

        // ex) "* com.hooby.service.*ServiceImpl.createUser(..)"
        // step 1: 리턴 타입
        String[] split1 = body.split(" ", 2);
        String returnType = split1[0].trim();

        // step 2: 전체 시그니처
        String signature = split1[1].trim();

        int paramStart = signature.indexOf("(");
        int paramEnd = signature.indexOf(")");
        String fullMethod = signature.substring(0, paramStart);
        String paramBody = signature.substring(paramStart + 1, paramEnd);

        // fullMethod: com.hooby.service.UserServiceImpl.createUser
        int lastDot = fullMethod.lastIndexOf(".");
        String classPattern = fullMethod.substring(0, lastDot);
        String methodPattern = fullMethod.substring(lastDot + 1);

        // parameter parsing
        List<String> paramPatterns = paramBody.equals("..") || paramBody.isEmpty()
                ? List.of("..")
                : List.of(paramBody.split("\\s*,\\s*"));

        return new ExecutionPointcut(classPattern, methodPattern, returnType, paramPatterns);
    }
}