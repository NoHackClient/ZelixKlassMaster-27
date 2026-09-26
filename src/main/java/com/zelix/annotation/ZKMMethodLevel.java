package com.zelix.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ZKMMethodLevel {
    FlowObfuscationPolicy obfuscateFlow() default FlowObfuscationPolicy.LIGHT;

    ExceptionObfuscationPolicy exceptionObfuscation() default ExceptionObfuscationPolicy.LIGHT;
}
