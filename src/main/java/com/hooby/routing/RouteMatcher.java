package com.hooby.routing;

import java.util.*;
import java.util.regex.*;

public class RouteMatcher {
    private final Pattern pattern;
    private final List<String> paramNames;

    public RouteMatcher(String routePattern) {
        paramNames = new ArrayList<>();

        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("^");

        for (String part : routePattern.split("/")) {
            if (part.isEmpty()) continue;
            regexBuilder.append("/");

            if (part.startsWith("{") && part.endsWith("}")) {
                String paramName = part.substring(1, part.length() - 1);
                paramNames.add(paramName);
                regexBuilder.append("([^/]+)");
            } else {
                regexBuilder.append(Pattern.quote(part));
            }
        }

        regexBuilder.append("/?"); // 슬래시 끝나도 허용
        regexBuilder.append("$");
        pattern = Pattern.compile(regexBuilder.toString());
    }

    public Map<String, String> match(String actualPath) {
        Matcher matcher = pattern.matcher(actualPath);
        if (!matcher.matches()) return null;

        Map<String, String> pathParams = new HashMap<>();
        for (int i = 0; i < matcher.groupCount(); i++) {
            pathParams.put(paramNames.get(i), matcher.group(i + 1));
        }
        return pathParams;
    }
}