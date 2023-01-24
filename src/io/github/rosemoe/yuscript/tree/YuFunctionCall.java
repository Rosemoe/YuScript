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
package io.github.rosemoe.yuscript.tree;

import io.github.rosemoe.yuscript.functions.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rose
 */
public class YuFunctionCall implements YuNode {

    private String functionName;

    public List<YuExpression> arguments;

    public Function resolvedFunction;

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitFunctionCall(this, value);
    }

    public YuFunctionCall() {
        arguments = new ArrayList<>();
    }

    public void addArgument(YuExpression expression) {
        arguments.add(expression);
    }

    /**
     * @return the arguments
     */
    public List<YuExpression> getArguments() {
        return arguments;
    }

    /**
     * @param functionName the functionName to set
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }

}
