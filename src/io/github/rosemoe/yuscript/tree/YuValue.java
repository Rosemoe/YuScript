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

/**
 * @author Rose
 */
public class YuValue implements YuNode {

    public final static int TYPE_VAR = 0, TYPE_NUM = 1, TYPE_STR = 2, TYPE_BOOL = 3, TYPE_NULL = 4;

    private String variableName;
    public String variablePrefix;
    public String variableKey;
    private String string;
    private Long number;
    private boolean bool;
    private int type = -1;
    private boolean invert = false;

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitValue(this, value);
    }

    /**
     * @param invert the invert to set
     */
    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    /**
     * @return the invert
     */
    public boolean isInvert() {
        return invert;
    }

    /**
     * @param variableName the variableName to set
     */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
        if (variableName.contains(".")) {
            int index = variableName.indexOf('.');
            variablePrefix = variableName.substring(0, index);
            variableKey = variableName.substring(index + 1);
        } else {
            variablePrefix = "s";
            variableKey = variableName;
        }
        type = TYPE_VAR;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(String number) {
        this.number = Long.parseLong(number);
        type = TYPE_NUM;
    }

    /**
     * @param string the string to set
     */
    public void setString(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < string.length() - 1; i++) {
            if (string.charAt(i) == '\\') {
                char next = string.charAt(i + 1);
                if (next == 'n') {
                    sb.append('\n');
                } else {
                    sb.append(next);
                }
                i++;
            } else {
                sb.append(string.charAt(i));
            }
        }
        this.string = sb.toString();
        type = TYPE_STR;
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @return the number
     */
    public Long getNumber() {
        return number;
    }

    /**
     * @return the variableName
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * @param bool the bool to set
     */
    public void setBool(boolean bool) {
        this.bool = bool;
        type = TYPE_BOOL;
    }

    /**
     * @return the bool
     */
    public boolean getBool() {
        return bool;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    public void setNull() {
        type = TYPE_NULL;
    }

    /**
     * Get value
     */
    public Object getValue(YuContext context) {
        switch (getType()) {
            case TYPE_NUM:
                return number;
            case TYPE_STR:
                return string;
            case TYPE_VAR:
                return context.getVariable(variablePrefix, variableKey);
            case TYPE_BOOL:
                return bool;
            case TYPE_NULL:
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
