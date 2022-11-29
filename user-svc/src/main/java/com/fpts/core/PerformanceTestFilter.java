package com.fpts.core;

import com.fpts.api.annotation.PerformanceTest;
import com.fpts.api.enums.HttpMethodEnum;
import com.fpts.api.model.TestSpec;
import com.fpts.api.util.JSONConverter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This is the main class that will be called to filter all requests send to the
 * services.<br>
 * This class will analyze the request method, case its an OPTIONS the test
 * specification ({@link TestSpec}) will be obtained from the service and send
 * as a response. <br>
 * To obtain the test specification this class will look for the method annoted
 * with {@link PerformanceTest} that contains the requested URI (e.g. /rest/crl)
 * in the service class.
 *
 * @author andre
 */
@WebFilter(urlPatterns = "/*")
public class PerformanceTestFilter implements Filter {

    static Logger logger = LoggerFactory.getLogger(PerformanceTestFilter.class);

    private static final String OPTIONS_METHOD_STR = "OPTIONS";

    private final RequestMappingHandlerMapping requestMappingHandler;
    private final ApplicationContext context;

    public PerformanceTestFilter(RequestMappingHandlerMapping requestMappingHandler, ApplicationContext context) {
        this.requestMappingHandler = requestMappingHandler;
        this.context = context;
    }

    private void extractTestSpec(RequestMappingHandlerMapping requestMappingHandler, ServletResponse response,
                                 String requestedPath) {

        for (var info : requestMappingHandler.getHandlerMethods().keySet()) {

            if (info.getPathPatternsCondition() == null) {
                continue;
            }

            var match = info.getPathPatternsCondition()
                    .getPatterns()
                    .stream()
                    .map(PathPattern::toString)
                    .anyMatch(s -> s.equals(requestedPath));

            if (!match) {
                continue;
            }

            HandlerMethod handler = requestMappingHandler.getHandlerMethods().get(info);

            List<Method> methodsWithTestAnnotation = getMethodsAnnotatedWith(handler.getBeanType(),
                    PerformanceTest.class);

            TestSpecification spec = null;
            for (Method method : methodsWithTestAnnotation) {
                PerformanceTest perfAnnotation = method.getAnnotation(PerformanceTest.class);
                HttpMethodEnum httpMethod = perfAnnotation.httpMethod();
                String description = perfAnnotation.description();
                String path = perfAnnotation.path();
                // Check if the requested path is the same as the one
                // annotated in the service method
                if (path.equals(requestedPath)) {
                    try {


                        TestSpec parametersForTest = (TestSpec) method.invoke(context.getBean(handler.getBeanType()));

                        String jsonSchema = JSONConverter
                                .getJsonSchema(parametersForTest.getTestParameter().getClass());
                        if (spec != null) {
                            TestSpecification.joinSpec(spec, httpMethod, description,
                                    parametersForTest.getTestParameter(), parametersForTest.getValidationData(),
                                    jsonSchema);
                        } else {
                            spec = TestSpecification.build(httpMethod, description,
                                    parametersForTest.getTestParameter(), parametersForTest.getValidationData(),
                                    jsonSchema);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Generated spec: " + spec.getSpec().toString());
                        }

                    } catch (Exception e) {
                        logger.error("Error while writing response to OPTIONS request", e);
                    }
                }
            }
            try {
                if (spec != null) {
                    response.getOutputStream().write(spec.getSpec().toString().getBytes());
                    return;
                }
            } catch (IOException e) {
                logger.error("Error while writing response to OPTIONS request", e);
            }
        }
    }

    private static List<Method> getMethodsAnnotatedWith(final Class<?> type,
                                                        final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        // iterate though the list of methods declared in the class
        // represented by klass variable, and add those annotated with the
        // specified annotation
        final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
        for (final Method method : allMethods) {
            if (method.isAnnotationPresent(annotation)) {
                methods.add(method);
            }
        }
        return methods;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String uri = httpRequest.getRequestURL().toString();

        if (logger.isDebugEnabled()) {
            logger.debug("Entering PerformanceTestFilter");
        }

        if (OPTIONS_METHOD_STR.equals(httpRequest.getMethod())) {
            logger.info("Working on response for OPTIONS request to: " + uri);
            extractTestSpec(this.requestMappingHandler, response, httpRequest.getServletPath());
            return;
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

}
