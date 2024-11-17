package com.joker2yue.frequencycontrol.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)// 运行时生效
public @interface FrequencyControlContainer {
    FrequencyControl[] value();
}