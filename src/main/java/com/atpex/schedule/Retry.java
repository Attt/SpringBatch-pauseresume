package com.atpex.schedule;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 * Created by Atpex on 2017/7/1.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Inherited
public @interface Retry {

    long milliseconds() default 1000;

    Class[] exceptions();

    String[] method();

}
