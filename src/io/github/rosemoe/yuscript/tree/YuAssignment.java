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

/**
 * @author Rose
 */
public class YuAssignment implements YuNode {

    private int variableType;
    private String variableName;
    private YuExpression value;

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitAssignment(this, value);
    }

    /**
     * @param value the value to set
     */
    public void setValue(YuExpression value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public YuExpression getValue() {
        return value;
    }

    /**
     * @param variableName the variableName to set
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    public void setVariableType(String variablePrefix) {
        switch (variablePrefix) {
            case "s":
                variableType = YuVariableType.LOCAL;
                break;
            case "ss":
                variableType = YuVariableType.SESSION;
                break;
            case "sss":
                variableType = YuVariableType.GLOBAL;
                break;
            default:
                throw new IllegalArgumentException("unknown variable type");
        }
    }

    public void setVariableType(int variableType) {
        this.variableType = variableType;
    }

    /**
     * @return the variableType
     */
    public int getVariableType() {
        return variableType;
    }

}
