/*
  This Java File is Created By Rose
 */
package com.rose.yuscript.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
/*
  @author Rose

 */
public @interface ScriptMethod {
	
	boolean returnValueAtBegin() default false;
	
}
