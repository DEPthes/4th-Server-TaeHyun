package com.hooby.servlet;

import com.hooby.routing.RouteMatcher;

import java.util.*;

public class ServletMapper {
    private final List<RouteBinding> routes = new ArrayList<>();

    private static class RouteBinding {
        final RouteMatcher matcher;
        final String servletName;

        RouteBinding(RouteMatcher matcher, String servletName) {
            this.matcher = matcher;
            this.servletName = servletName;
        }
    }

    public ServletMapper(Map<String, String> routeMap) {
        for (Map.Entry<String, String> entry : routeMap.entrySet()) {
            registerServlet(entry.getKey(), entry.getValue());
        }
    }

    public void registerServlet(String routePattern, String servletName) {
        routes.add(new RouteBinding(new RouteMatcher(routePattern), servletName));
    }

    public MappingResult map(String requestPath) {
        for (RouteBinding binding : routes) {
            Map<String, String> params = binding.matcher.match(requestPath);
            if (params != null) {
                return new MappingResult(binding.servletName, params);
            }
        }
        return null;
    }

    public record MappingResult(String servletName, Map<String, String> pathParams) {}
}