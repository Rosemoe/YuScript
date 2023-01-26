package io.github.rosemoe.yuscript.tree;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface YuVariableType {

    int LOCAL = 0;
    int SESSION = 1;
    int GLOBAL = 2;

}
