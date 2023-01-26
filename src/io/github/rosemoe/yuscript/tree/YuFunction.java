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

import io.github.rosemoe.yuscript.YuContext;
import io.github.rosemoe.yuscript.YuInterpreter;
import io.github.rosemoe.yuscript.functions.Function;

import java.util.ArrayList;
import java.util.List;

public class YuFunction implements YuNode, Function {

    private String name;

    private final List<String> parameterNames = new ArrayList<>();

    private final List<Integer> returnPositions = new ArrayList<>();

    private YuCodeBlock functionBody;

    public String getName() {
        return name;
    }

    @Override
    public int getArgumentCount() {
        return getParameterCount();
    }

    @Override
    public void invoke(List<YuExpression> arguments, YuCodeBlock additionalCodeBlock, YuContext context, YuInterpreter interpreter) throws Throwable {
        YuContext newContext = YuContext.obtain(context.getSession());
        newContext.setDeclaringInterpreter(context.getDeclaringInterpreter());
        for (int i = 0; i < getParameterCount(); i++) {
            String name = getParameterNames().get(i);
            Object value = arguments.get(i).getValue(context);
            newContext.setVariable(YuVariableType.LOCAL, name, value);
        }
        interpreter.visitCodeBlock(getFunctionBody(), newContext);
        List<Integer> returnPositions = getReturnPositions();
        for (int i = 0; i < returnPositions.size(); i++) {
            Integer position = returnPositions.get(i);
            YuExpression paramExpr = arguments.get(position);
            if (paramExpr.getOperators().size() == 0) {
                YuValue valueObj = paramExpr.getChildren().get(0);
                if (valueObj.getType() == YuValue.TYPE_VAR) {
                    Object value = newContext.getVariable(YuVariableType.LOCAL, getParameterNames().get(position));
                    context.setVariable(valueObj.variableType, valueObj.variableKey, value);
                }
            }
        }
        YuContext.recycle(newContext);
    }

    public List<Integer> getReturnPositions() {
        return returnPositions;
    }

    public void markReturnPosition() {
        returnPositions.add(getParameterCount() - 1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addParameter(String name) {
        parameterNames.add(name);
    }

    public int getParameterCount() {
        return parameterNames.size();
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public void setFunctionBody(YuCodeBlock functionBody) {
        this.functionBody = functionBody;
    }

    public YuCodeBlock getFunctionBody() {
        return functionBody;
    }

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitFunction(this, value);
    }

}
