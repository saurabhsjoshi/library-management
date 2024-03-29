package com.fpts.api.annotation;

import com.fpts.api.enums.HttpMethodEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the main annotation used to specify that a particular method will
 * return a performance test specification.<br>
 * The compiler will check if the method with this annotation is returning a
 * class of type {@link TestSpec}
 *
 * @author andre
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PerformanceTest {

    HttpMethodEnum httpMethod() default HttpMethodEnum.GET;

    String description() default "description";

    String path();

}