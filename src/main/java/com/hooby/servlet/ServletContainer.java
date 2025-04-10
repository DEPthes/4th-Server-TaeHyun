package com.hooby.servlet;

import com.hooby.http.CustomHttpRequest;
import com.hooby.http.CustomHttpResponse;
import com.hooby.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContainer {
    private static final Logger logger = LoggerFactory.getLogger(ServletContainer.class);

    private final ServletMapper servletMapper;
    private final ServletInitializer servletInitializer;

    public ServletContainer(ServletMapper servletMapper, ServletInitializer servletInitializer) {
        this.servletMapper = servletMapper;
        this.servletInitializer = servletInitializer;
    }

    public CustomHttpResponse dispatch(CustomHttpRequest request) {
        CustomHttpResponse response = new CustomHttpResponse();

        ServletMapper.MappingResult result = servletMapper.map(request.getPath());

        if (result == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("Not Found");
        } else {
            request.setPathParams(result.pathParams());
            Servlet servlet = servletInitializer.getOrCreate(result.servletName());
            servlet.service(request, response);
        }

        return response;
    }
}
