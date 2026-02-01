package com.words.basesdk.aspect;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Track {
    String serviceName() default "";
    String requestClassName() default "";
}