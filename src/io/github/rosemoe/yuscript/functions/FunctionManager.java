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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.rosemoe.yuscript.annotation.ScriptMethod;

/**
 * @author Rose
 * A manager that holds all script functions which will used by interpreter
 */
public class FunctionManager {

    private final static boolean isMethodHandleSupported;

    static {
        //MethodHandle seems to be slower than direct reflection
		/*
		boolean methodHandleFound = false;
		try {
			Class.forName("java.lang.invoke.MethodHandle");
			methodHandleFound = true;
		}catch (ClassNotFoundException ignored) {
		}*/
        isMethodHandleSupported = false;
    }

    private final Map<String, List<Function>> functionMap;

    private final Map<String, YuModule> modules;

    /**
     * Create a FunctionManager and add all basic functions
     */
    public FunctionManager() {
        functionMap = new HashMap<>();
        modules = new HashMap<>();
        try {
            addFunctionsFromClass(YuMethod.class);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // Add direct functions instead of reflecting them to get quicker speed
        // It is not recommended to add functions by reflection or method handles because its speed can be quite slow
        addFunction(Function_s.INSTANCE);
        addFunction(Function_sn.INSTANCE);
        addFunction(Function_syso.INSTANCE);
    }

    /**
     * Add all script functions of a class
     *
     * @param clazz The class
     */
    public void addFunctionsFromClass(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(ScriptMethod.class)) {
                addFunctionFromMethod(method);
            }
        }
    }

    /**
     * Make this method as a script function
     * NOTE:
     * The method must be public,static and non-abstract and with a ScriptMethod annotation.
     * Otherwise,it will throw a IllegalArgumentException.
     * NOTE:
     * One method could be added for several times
     * DOT NOT add a method twice or more
     *
     * @param method The method to add
     */
    public void addFunctionFromMethod(Method method) throws NoSuchMethodException, IllegalAccessException {
        if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
            throw new IllegalArgumentException("Only public method can be added");
        }
        if ((method.getModifiers() & Modifier.STATIC) == 0) {
            throw new IllegalArgumentException("Only static method can be added");
        }
        if ((method.getModifiers() & Modifier.ABSTRACT) != 0) {
            throw new IllegalArgumentException("Only non-abstract method can be added");
        }
        if (!method.isAnnotationPresent(ScriptMethod.class)) {
            throw new IllegalArgumentException("Method must represent ScriptMethod annotation");
        }
        addFunction(isMethodHandleSupported ? new JavaInvokeFunction(method) : new JavaReflectFunction(method));
    }

    /**
     * Remove the given function
     *
     * @param function Function to remove
     */
    public void removeFunction(Function function) {
        List<Function> funcs = functionMap.get(function.getName());
        if (funcs != null) {
            funcs.remove(function);
        }
    }

    /**
     * Add a new function
     *
     * @param function New function
     */
    public void addFunction(Function function) {
        functionMap.computeIfAbsent(function.getName(), (name) -> new ArrayList<Function>(4)).add(function);
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

    public void putModule(YuModule module) {
        modules.put(module.getName(), module);
    }

    public YuModule getModule(String name) {
        return modules.get(name);
    }

}
