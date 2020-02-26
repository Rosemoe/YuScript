/**
 * This Java File is Created By Rose
 */
package com.rose.yuscript.functions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rose.yuscript.annotation.ScriptMethod;

/**
 * @author Rose
 *
 */
public class FunctionManager {

	private Map<String,List<Function>> functionMap;
	
	public FunctionManager() {
		functionMap = new HashMap<>();
		addFunctionsFromClass(YuMethod.class);
	}
	
	public void addFunctionsFromClass(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		for(Method method : methods) {
			if(method.isAnnotationPresent(ScriptMethod.class)) {
				addFunctionFromMethod(method);
			}
		}
	}
	
	public void addFunctionFromMethod(Method method) {
		if((method.getModifiers() & Modifier.PUBLIC) == 0) {
			throw new IllegalArgumentException("Only public method can be added");
		}
		if((method.getModifiers() & Modifier.STATIC) == 0) {
			throw new IllegalArgumentException("Only static method can be added");
		}
		if((method.getModifiers() & Modifier.ABSTRACT) != 0) {
			throw new IllegalArgumentException("Only non-abstract method can be added");
		}
		if(!method.isAnnotationPresent(ScriptMethod.class)) {
			throw new IllegalArgumentException("Method must represent ScriptMethod annotation");
		}
		addFunction(new JavaFunction(method));
	}
	
	public void removeFunctions(String name) {
		functionMap.remove(name);
	}
	
	public void removeFunction(Function function) {
		List<Function> list = functionMap.get(function.getName());
		if(list == null) {
			return;
		}
		list.remove(function);
	}
	
	public void addFunction(Function function) {
		List<Function> list = functionMap.get(function.getName());
		if(list == null) {
			list = new ArrayList<>();
			functionMap.put(function.getName(), list);
		}
		if(list.contains(function)) {
			throw new IllegalArgumentException("Function has been added");
		}
		list.add(function);
	}
	
	public List<Function> getFunctions(String name) {
		List<Function> functions = functionMap.get(name);
		return Collections.unmodifiableList(functions != null ? functions : Collections.emptyList());
	}

}
