package com.renhui.component.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ScrollMode {

    int NONE = 0;

    int VOLUME = 1;

    int BRIGHTNESS = 2;
}