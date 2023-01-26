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
package io.github.rosemoe.yuscript.functions;

import java.util.List;

import io.github.rosemoe.yuscript.YuContext;
import io.github.rosemoe.yuscript.YuInterpreter;
import io.github.rosemoe.yuscript.tree.YuCodeBlock;
import io.github.rosemoe.yuscript.tree.YuExpression;

/**
 * @author Rose
 * Function interface for FunctionManager
 */
public interface Function {

    /**
     * Get the name of function
     *
     * @return function's name
     */
    String getName();

    /**
     * Argument count required.
     * NOTE:
     * -1 for unspecified (Any count)
     * NOTE:
     * Return value is a part of arguments
     * How to use arguments to set return value is up to you
     */
    int getArgumentCount();

    /**
     * Invoke this function
     *
     * @param arguments   Arguments to this function.Always matches this function's argument count
     * @param context     Current context
     * @param interpreter Current interpreter
     * @throws Throwable If any error generated
     */
    void invoke(List<YuExpression> arguments, YuCodeBlock additionalCodeBlock, YuContext context, YuInterpreter interpreter) throws Throwable;

}
