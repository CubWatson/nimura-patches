package com.llamalad7.mixinextras.injector;
import java.lang.annotation.*;
import org.spongepowered.asm.mixin.injection.At;
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface ModifyReturnValue { String[] method(); At[] at(); boolean remap() default true; int require() default -1; int expect() default 1; int allow() default -1; }
