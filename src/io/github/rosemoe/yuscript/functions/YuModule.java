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

import io.github.rosemoe.yuscript.tree.YuTree;

import java.util.*;

/**
 * A module is just like 'myu' file.
 * Modules are able to have its functions in a isolated place and can be invoked anywhere with module name.
 * Name of a module should be determined when the module is being created.
 * But functions in module can be changed at any time as long as there is no request to modify the function map concurrently.
 *
 * @author Rose
 */
public class YuModule {

    private final String name;

    private final Map<String, List<Function>> functionMap;

    public YuModule(String name) {
        this.name = Objects.requireNonNull(name);
        functionMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addTree(YuTree tree) {
        for (Function function : tree.getRoot().getFunctions()) {
            addFunction(function);
        }
    }

    public void addFunction(Function function) {
        functionMap.computeIfAbsent(function.getName(), (name) -> new ArrayList<>(4)).add(function);
    }

    public Function getFunction(String functionName, int parameterCount) {
        List<Function> functions = functionMap.get(functionName);
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
