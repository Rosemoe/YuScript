package com.rose.yuscript.tree;

import com.rose.yuscript.YuContext;
import com.rose.yuscript.YuInterpreter;
import com.rose.yuscript.functions.Function;

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
    public void invoke(List<YuExpression> arguments, YuContext context, YuInterpreter interpreter) throws Throwable {
        YuContext newContext = new YuContext(context, false);
        for(int i = 0;i < getParameterCount();i++) {
            String name = getParameterNames().get(i);
            Object value = arguments.get(i).getValue(context);
            newContext.setVariable(name, value);
        }
        interpreter.visitCodeBlock(getFunctionBody(), newContext);
        for(Integer position : getReturnPositions()) {
            if(position != null && position >= 0) {
                YuExpression paramExpr = arguments.get(position);
                if(paramExpr.getOperators().size() == 0) {
                    YuValue valueObj = paramExpr.getChildren().get(0);
                    if(valueObj.getType() == YuValue.TYPE_VAR) {
                        Object value = newContext.getVariable(getParameterNames().get(position));
                        context.setVariable(valueObj.getVariableName(), value);
                    }
                }
            }
        }
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
