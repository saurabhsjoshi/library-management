package com.fpts.api.filter;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Class that builds thru reflection the web filter during runtime. Can be the
 * {@link DummyFilter} if the core library is not available. <br>
 * Otherwise will be the web filter from the core library.
 *
 * @author andre
 */
public class FilterBuilder {

    public static jakarta.servlet.Filter buildPerformanceFilter(RequestMappingHandlerMapping requestMappingHandler,
                                                                ApplicationContext context) {
        Class filter;
        try {
            filter = Class.forName("com.fpts.core.PerformanceTestFilter");
        } catch (Exception e) {
            e.printStackTrace();
            // Filter not available
            return new DummyFilter();
        }
        try {
            return (jakarta.servlet.Filter) filter.getConstructor(RequestMappingHandlerMapping.class, ApplicationContext.class)
                    .newInstance(requestMappingHandler, context);
        } catch (Exception e) {
            return new DummyFilter();
        }
    }

}
