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
package io.github.rose2073.yuscript;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import io.github.rose2073.yuscript.functions.FunctionManager;
import io.github.rose2073.yuscript.tree.YuCodeBlock;
import io.github.rose2073.yuscript.tree.YuFunction;

/**
 * The context used to save and manage variables and call stack in script environment
 * @author Rose
 */
public class YuContext {

	private static final Map<Integer,Map<String,Object>> sessionVariableMaps;
	
	private static final Map<String,Object> globalVariables;
	
	static {
		globalVariables = new ConcurrentHashMap<>();
		sessionVariableMaps = new ConcurrentHashMap<>();
	}
	
	private synchronized static Map<String,Object> getSessionVariableMap(int session) {
		Map<String,Object> map = sessionVariableMaps.get(session);
		if(map == null) {
			map = new ConcurrentHashMap<>();
			sessionVariableMaps.put(session, map);
		}
		return map;
	}
	
	private final Map<String,Object> sessionVariables;
	private final Map<String,Object> localVariables;
	private final Stack<YuCodeBlock> stack;
	private final Stack<Boolean> usage;
	private final int session;
	private boolean stopFlag = false;
	public final static YuCodeBlock NO_CODE_BLOCK = new YuCodeBlock();
	private YuInterpreter declaringInterpreter;
	private final Stack<BoolWrapper> loopEnv = new Stack<>();
	private final Stack<YuCodeBlock> executeStack = new Stack<>();

	public void enterCodeBlock(YuCodeBlock codeBlock) {
		executeStack.add(codeBlock);
	}

	public void exitCodeBlock() {
		executeStack.pop();
	}

	public YuFunction findFunctionFromScope(String name, int paramCount) {
		String functionId = FunctionManager.getFunctionID(name, paramCount);
		for(int i = executeStack.size() - 1;i >= 0;i--) {
			YuCodeBlock codeBlock = executeStack.get(i);
			YuFunction function;
			if((function = codeBlock.getFunction(functionId)) != null) {
				return function;
			}
		}
		return null;
	}

	/**
	 * Wrapper class
	 */
	private static class BoolWrapper {
		public boolean value = false;
	}

	/**
	 * Attach code block
	 * @param block code block attach to current statement
	 */
	public void addCodeBlock(YuCodeBlock block) {
		stack.push(block);
		usage.push(false);
	}

	/**
	 * Enter a new loop
	 */
	public void enterLoop() {
		loopEnv.add(new BoolWrapper());
	}

	/**
	 * Exit from loop
	 */
	public void exitLoop() {
		loopEnv.pop();
	}

	/**
	 * Whether in a loop
	 * @return whether in a loop
	 */
	public boolean isInLoop() {
		return !loopEnv.empty();
	}

	/**
	 * Break current loop
	 */
	public void loopBreak() {
		loopEnv.peek().value = true;
	}

	/**
	 * Whether the top code block is used
	 * @return whether used
	 */
	public boolean isCodeBlockUsed() {
		return usage.lastElement();
	}

	/**
	 * Whether there is code block
	 * @return code block available
	 */
	public boolean hasCodeBlock() {
		return (!stack.isEmpty()) && stack.lastElement() != NO_CODE_BLOCK;
	}

	/**
	 * Get code block attached to current statement
	 * @return Code block near by current statement
	 */
	public YuCodeBlock getCodeBlock() {
		YuCodeBlock block = hasCodeBlock() ? stack.lastElement() : null;
		if(block != null) {
			usage.pop();
			usage.push(true);
		}
		return block;
	}

	/**
	 * Exit from a code block
	 */
	public void popCodeBlock() {
		stack.pop();
		usage.pop();
	}
	
	/**
	 * @param declaringInterpreter the declaringInterpreter to set
	 */
	protected void setDeclaringInterpreter(YuInterpreter declaringInterpreter) {
		this.declaringInterpreter = declaringInterpreter;
	}
	
	/**
	 * @return the declaringInterpreter
	 */
	public YuInterpreter getDeclaringInterpreter() {
		return declaringInterpreter;
	}
	
	/**
	 * @param stopFlag the stopFlag to set
	 */
	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}
	
	/**
	 * @return the stopFlag
	 */
	public boolean isStopFlagSet() {
		return stopFlag || (!loopEnv.empty() && loopEnv.peek().value);
	}
	
	/**
	 * Create a empty context with the given session
	 * @param session Session for variable management
	 */
	public YuContext(int session) {
		sessionVariables = getSessionVariableMap(session);
		localVariables = new HashMap<>();
		stack = new Stack<>();
		usage = new Stack<>();
		this.session = session;
	}

	/**
	 * Create a new context from the given context
	 * @param context The source context.Can not be null
	 */

	public YuContext(YuContext context) {
		this(context, true, true);
	}

	@SuppressWarnings("CopyConstructorMissesField")
	public YuContext(YuContext context, boolean copyLocalVariables, boolean copyStack) {
		this(context.getSession());
		if(copyLocalVariables) {
			localVariables.putAll(context.localVariables);
		}
		if(copyStack) {
			executeStack.addAll(context.executeStack);
		}
		declaringInterpreter = context.declaringInterpreter;
	}
	
	/**
	 * Get session of this context
	 * @return Session of this context
	 */
	public int getSession() {
		return session;
	}
	
	/**
	 * Get the variable map for the given prefix
	 * @param prefix The name of map
	 * @return The variable map
	 */
	private Map<String,Object> getVariableMapForPrefix(String prefix) {
		switch(prefix) {
		case "s":
			return localVariables;
		case "ss":
			return sessionVariables;
		case "sss":
			return globalVariables;
		}
		throw new IllegalArgumentException("Not a valid variable type:" + prefix);
	}
	
	/**
	 * Set variable value
	 * @param prefix The name of map
	 * @param name The name of variable without its map's name
	 * @param value The value of variable
	 */
	public void setVariable(String prefix,String name,Object value) {
		Map<String,Object> map = getVariableMapForPrefix(prefix);
		if(name.contains(".")) {
			throw new IllegalArgumentException("dot in name is a syntax error");
		}
		if(value == null) {
			map.remove(name);
			return;
		}
		map.put(name, value);
	}
	
	public void setVariable(String fullName,Object val) {
		int dot = fullName.indexOf(".");
		if(dot == -1) {
			setVariable("s", fullName,val);
			return;
		}
		int lastDot = fullName.lastIndexOf(".");
		if(dot != lastDot) {
			throw new IllegalArgumentException("two or more dots in name is a syntax error");
		}
		setVariable(fullName.substring(0,dot), fullName.substring(dot + 1),val);
	}
	
	/**
	 * Get variable value
	 * @param prefix The name of map
	 * @param name The name of variable without its map's name
	 * @return The value of variable
	 */
	public Object getVariable(String prefix,String name) {
		return getVariableMapForPrefix(prefix).get(name);
	}
	
	/**
	 * Get variable value
	 * @param fullName The full name of variable such as ss.var
	 * @return The value of variable
	 */
	public Object getVariable(String fullName) {
		int dot = fullName.indexOf(".");
		if(dot == -1) {
			return getVariable("s", fullName);
		}
		int lastDot = fullName.lastIndexOf(".");
		if(dot != lastDot) {
			throw new IllegalArgumentException("two or more dots in name is a syntax error");
		}
		return getVariable(fullName.substring(0,dot), fullName.substring(dot + 1));
	}

}
