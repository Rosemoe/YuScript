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
package io.github.rosemoe.yuscript;

import io.github.rosemoe.yuscript.tree.YuCodeBlock;
import io.github.rosemoe.yuscript.tree.YuFunction;
import io.github.rosemoe.yuscript.tree.YuVariableType;
import io.github.rosemoe.yuscript.util.LocalStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The context used to save and manage variables and call stack in script environment
 *
 * @author Rose
 */
public class YuContext {
    private final static int CACHE_SIZE = 32;
    private static int cacheCount;
    private final static YuContext[] sCache = new YuContext[CACHE_SIZE];

    public static YuContext obtain(int session) {
        synchronized (YuContext.class) {
            if (cacheCount > 0) {
                YuContext context = sCache[--cacheCount];
                sCache[cacheCount] = null;
                if (context.session != session) {
                    context.session = session;
                    context.sessionVariables = getSessionVariableMap(session);
                }
                return context;
            }
        }
        return new YuContext(session);
    }

    public static void recycle(YuContext context) {
        context.reset();
        synchronized (YuContext.class) {
            if (cacheCount < CACHE_SIZE) {
                sCache[cacheCount++] = context;
            }
        }
    }

    public static void clearSession(int session) {
        sessionVariableMaps.remove(session);
        synchronized (YuContext.class) {
            for (int i = 0; i < CACHE_SIZE; i++) {
                if (sCache[i].session == session) {
                    sCache[i] = null;
                }
            }
        }
    }

    private static final Map<Integer, Map<String, Object>> sessionVariableMaps;

    private static final Map<String, Object> globalVariables;

    static {
        globalVariables = new ConcurrentHashMap<>();
        sessionVariableMaps = new ConcurrentHashMap<>();
    }

    private static Map<String, Object> getSessionVariableMap(int session) {
        Map<String, Object> map = sessionVariableMaps.get(session);
        if (map == null) {
            synchronized (YuContext.class) {
                map = sessionVariableMaps.get(session);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    sessionVariableMaps.put(session, map);
                }
            }
        }
        return map;
    }

    private Map<String, Object> sessionVariables;
    private final Map<String, Object> localVariables;
    private int session;
    private boolean stopFlag = false;
    public final static YuCodeBlock NO_CODE_BLOCK = new YuCodeBlock();
    private YuInterpreter declaringInterpreter;
    private final LocalStack<BoolWrapper> loopEnv = new LocalStack<>();
    private final LocalStack<YuCodeBlock> functionSearchScopes = new LocalStack<>();
    private final Object[] fastLocals = new Object[26];

    public void pushFunctionSearchScope(YuCodeBlock codeBlock) {
        functionSearchScopes.add(codeBlock);
    }

    public void popFunctionSearchScope() {
        functionSearchScopes.pop();
    }

    public YuFunction findFunctionFromScope(String name, int paramCount) {
        for (int i = functionSearchScopes.size() - 1; i >= 0; i--) {
            YuCodeBlock codeBlock = functionSearchScopes.get(i);
            YuFunction function;
            if ((function = codeBlock.getFunction(name, paramCount)) != null) {
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
     *
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
     * @param declaringInterpreter the declaringInterpreter to set
     */
    public void setDeclaringInterpreter(YuInterpreter declaringInterpreter) {
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
     * Create an empty context with the given session
     *
     * @param session Session for variable management
     */
    public YuContext(int session) {
        sessionVariables = getSessionVariableMap(session);
        localVariables = new HashMap<>();
        this.session = session;
    }

    /**
     * Create a new context from the given context
     *
     * @param context The source context.Can not be null
     */

    public YuContext(YuContext context) {
        this(context, true, true);
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public YuContext(YuContext context, boolean copyLocalVariables, boolean copyStack) {
        this(context.getSession());
        if (copyLocalVariables) {
            localVariables.putAll(context.localVariables);
        }
        if (copyStack) {
            functionSearchScopes.addAll(context.functionSearchScopes);
        }
        declaringInterpreter = context.declaringInterpreter;
    }

    /**
     * Get session of this context
     *
     * @return Session of this context
     */
    public int getSession() {
        return session;
    }

    /**
     * Get the variable map for the given prefix
     *
     * @return The variable map
     */
    private Map<String, Object> getVariableMapForType(@YuVariableType int type) {
        switch (type) {
            case YuVariableType.LOCAL:
                return localVariables;
            case YuVariableType.SESSION:
                return sessionVariables;
            case YuVariableType.GLOBAL:
                return globalVariables;
        }
        throw new IllegalArgumentException("Not a valid variable type:" + type);
    }

    /**
     * Set variable value
     *
     * @param name   The name of variable without its map's name
     * @param value  The value of variable
     */
    public void setVariable(@YuVariableType int type, String name, Object value) {
        if (type == YuVariableType.LOCAL && name.length() == 1) {
            char ch = name.charAt(0);
            if (ch >= 'a' && ch <= 'z') {
                fastLocals[ch - 'a'] = value;
                return;
            }
        }
        Map<String, Object> map = getVariableMapForType(type);
        if (value == null) {
            map.remove(name);
            return;
        }
        map.put(name, value);
    }

    /**
     * Get variable value
     *
     * @param name   The name of variable without its map's name
     * @return The value of variable
     */
    public Object getVariable(@YuVariableType int type, String name) {
        if (type == YuVariableType.LOCAL && name.length() == 1) {
            char ch = name.charAt(0);
            if (ch >= 'a' && ch <= 'z') {
                return fastLocals[ch - 'a'];
            }
        }
        return getVariableMapForType(type).get(name);
    }

    public void reset() {
        loopEnv.clear();
        functionSearchScopes.clear();
        declaringInterpreter = null;
        stopFlag = false;
        localVariables.clear();
        System.arraycopy(EMPTY_LOCALS, 0, fastLocals, 0, 26);
    }

    private final static Object[] EMPTY_LOCALS = new Object[26];

}
