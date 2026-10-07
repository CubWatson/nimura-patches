package com.llamalad7.mixinextras.injector.wrapmethod;
import java.lang.annotation.*;
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface WrapMethod { String[] method(); boolean remap() default true; int require() default -1; int expect() default 1; int allow() default -1; }
