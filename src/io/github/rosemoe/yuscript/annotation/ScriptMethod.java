/*
 * Copyright 2020 Rose2073
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.rosemoe.yuscript.annotation;

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

    /**
     * the position of return value
     *
     * @return begin of end
     */
    boolean returnValueAtBegin() default false;

    /**
     * Name in script env.
     * default is method's name
     * This can support special character used in iyu such as s+ and s-
     *
     * @return name
     */
    String scriptEnvName() default "@DEFAULT";

}
