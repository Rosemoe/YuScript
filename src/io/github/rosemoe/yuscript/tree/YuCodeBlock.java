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

import java.util.*;

/**
 * @author Rose
 */
public class YuCodeBlock implements YuNode {

    private final List<YuNode> children;

    private final List<YuFunction> functions;

    private final Map<String, List<YuFunction>> functionMap = new HashMap<>();

    @Override
    public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
        return visitor.visitCodeBlock(this, value);
    }

    public YuCodeBlock() {
        children = new ArrayList<>();
        functions = new ArrayList<>();
    }

    public void addChild(YuNode child) {
        children.add(Objects.requireNonNull(child));
    }

    public List<YuNode> getChildren() {
        return children;
    }

    public List<YuFunction> getFunctions() {
        return functions;
    }

    public void addFunction(YuFunction function) {
        functions.add(function);
        functionMap.computeIfAbsent(function.getName(), (name) -> new ArrayList<>(4)).add(function);
    }

    public YuFunction getFunction(String functionName, int parameterCount) {
        List<YuFunction> functions = functionMap.get(functionName);
        if (functions == null) {
            return null;
        }
        int size = functions.size();
        for (int i = 0; i < size; i++) {
            if (functions.get(i).getArgumentCount() == parameterCount) {
                return functions.get(i);
            }
        }
        return null;
    }

}
