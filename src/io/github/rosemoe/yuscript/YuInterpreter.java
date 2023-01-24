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

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;

import io.github.rosemoe.yuscript.functions.Function;
import io.github.rosemoe.yuscript.functions.FunctionManager;
import io.github.rosemoe.yuscript.functions.YuModule;
import io.github.rosemoe.yuscript.tree.*;

/**
 * @author Rose
 * Base Interpreter for yu script
 */
public class YuInterpreter implements YuTreeVisitor<Void, YuContext> {

    private final int session;

    private FunctionManager functionManager;

    public YuInterpreter(int session) {
        this(session, new FunctionManager());
    }

    public YuInterpreter(int session, FunctionManager functionManager) {
        this.session = session;
        setFunctionManager(functionManager);
    }

    /**
     * Set the function manager
     * This method should be called when no evaluation is in progress
     *
     * @param functionManager New function manager
     */
    public void setFunctionManager(FunctionManager functionManager) {
        this.functionManager = Objects.requireNonNull(functionManager);
    }

    /**
     * @return the mgr
     */
    public FunctionManager getFunctionManager() {
        return functionManager;
    }

    /**
     * @return the session
     */
    public int getSession() {
        return session;
    }

    public void eval(String code) {
        eval(new YuTree(new YuTokenizer(code)));
    }

    public void eval(YuTree tree) {
        eval(tree, new YuContext(getSession()));
    }

    public void eval(YuTree tree, YuContext context) {
        if (tree == null || context == null) {
            throw new IllegalArgumentException("argument(s) can not be null");
        }
        if (context.getDeclaringInterpreter() != null && context.getDeclaringInterpreter() != this) {
            throw new IllegalArgumentException("bad context:context is using by another interpreter instance");
        }
        context.setDeclaringInterpreter(this);
        tree.getRoot().accept(this, context);
        context.setDeclaringInterpreter(null);
    }

    @Override
    public Void visitAssignment(YuAssignment assign, YuContext value) {
        value.setVariable(assign.getVariableType(), assign.getVariableName(), assign.getValue().getValue(value));
        return null;
    }

    @Override
    public Void visitScope(YuScope scope, YuContext value) {
        //Now just like a code block
        visitCodeBlock(scope, value);
        return null;
    }

    @Override
    public Void visitCodeBlock(YuCodeBlock codeBlock, YuContext value) {
        boolean hasFunctionDefs = codeBlock.getFunctions().size() > 0;
        if (hasFunctionDefs) {
            value.pushFunctionSearchScope(codeBlock);
        }
        List<YuNode> nodes = codeBlock.getChildren();
        int size = nodes.size();
        YuCodeBlock lastPushed = YuContext.NO_CODE_BLOCK;
        value.pushCodeBlock(lastPushed);
        if (size > 0) {
            YuNode next = nodes.get(0);
            for (int i = 0; i < size && !value.isStopFlagSet(); i++) {
                YuNode child = next;
                next = i + 1 == size ? null : nodes.get(i + 1);
                YuCodeBlock toBeProvided = YuContext.NO_CODE_BLOCK;
                if (next instanceof YuCodeBlock) {
                    toBeProvided = (YuCodeBlock) next;
                }
                if (toBeProvided != lastPushed) {
                    value.popCodeBlock();
                    value.pushCodeBlock(toBeProvided);
                    lastPushed = toBeProvided;
                }
                child.accept(this, value);
                if (toBeProvided != YuContext.NO_CODE_BLOCK && value.isCodeBlockUsed()) {
                    i++;
                    if (i + 1 < size) {
                        next = nodes.get(i + 1);
                    }
                }
            }
        }
        value.popCodeBlock();
        if (hasFunctionDefs) {
            value.popFunctionSearchScope();
        }
        return null;
    }

    @Override
    public Void visitEndcode(YuEndcode endcode, YuContext value) {
        value.setStopFlag(true);
        return null;
    }

    public static String stringForm(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj == null ? "null" : String.valueOf(obj);
    }

    private static Long castToLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        String strF = stringForm(obj);
        try {
            return Long.parseLong(strF);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public Void visitForTree(YuForTree tree, YuContext value) {
        Object left = tree.getDest().getValue(value);
        Object right = tree.getSrc().getValue(value);
        value.enterLoop();
        if (castToLong(left) != null && castToLong(right) != null) {
            long max = castToLong(right);
            for (long i = castToLong(left); i <= max && !value.isStopFlagSet(); i++) {
                tree.getCodeBlock().accept(this, value);
            }
        } else if (right != null && right.getClass().isArray()) {
            int length = Array.getLength(right);
            for (int i = 0; i < length; i++) {
                if (value.isStopFlagSet()) {
                    break;
                }
                YuValue target = tree.getDest();
                if (target.getType() == YuValue.TYPE_VAR) {
                    value.setVariable(target.variablePrefix, target.variableKey, Array.get(right, i));
                }
                tree.getCodeBlock().accept(this, value);
            }
        } else if (right instanceof Iterable) {
            Iterable<?> r = (Iterable<?>) right;
            for (Object val : r) {
                if (value.isStopFlagSet()) {
                    break;
                }
                YuValue target = tree.getDest();
                if (target.getType() == YuValue.TYPE_VAR) {
                    value.setVariable(target.variablePrefix, target.variableKey, val);
                }
                tree.getCodeBlock().accept(this, value);
            }
        } else {
            System.err.println("Incompatible type for FOR loop");
        }
        value.exitLoop();
        return null;
    }

    @Override
    public Void visitFunctionCall(YuFunctionCall call, YuContext value) {
        Function function = call.resolvedFunction;
        if (function != null) {
            // Fast call
            invokeFunction(function, call, value);
            return null;
        }
        function = value.findFunctionFromScope(call.getFunctionName(), call.arguments.size());
        if (function != null) {
            invokeFunction(function, call, value);
            call.resolvedFunction = function;
            return null;
        }
        function = functionManager.getFunction(call.getFunctionName(), call.arguments.size());
        if (function != null) {
            invokeFunction(function, call, value);
            call.resolvedFunction = function;
            return null;
        }
        function = functionManager.getFunction(call.getFunctionName(), -1);
        if (function != null) {
            invokeFunction(function, call, value);
            call.resolvedFunction = function;
            return null;
        }
        throw new YuSyntaxError("no such method:" + call.getFunctionName() + " with argument count " + call.getArguments().size());
    }

    @Override
    public Void visitIfTree(YuIfTree tree, YuContext value) {
        if (tree.getCondition().getValue(value)) {
            tree.getCodeBlock().accept(this, value);
        } else if (tree.getFallbackCodeBlock() != null) {
            tree.getFallbackCodeBlock().accept(this, value);
        }
        return null;
    }

    @Override
    public Void visitWhileTree(YuWhileTree tree, YuContext value) {
        value.enterLoop();
        while (tree.getCondition().getValue(value) && !value.isStopFlagSet()) {
            tree.getCodeBlock().accept(this, value);
        }
        value.exitLoop();
        return null;
    }

    @Override
    public Void visitExpression(YuExpression expr, YuContext value) {
        //This will be handled by other functions
        return null;
    }

    @Override
    public Void visitValue(YuValue val, YuContext value) {
        //This will be handled by other functions
        return null;
    }

    @Override
    public Void visitBreak(YuBreak codeBlock, YuContext value) {
        if (value.isInLoop()) {
            value.loopBreak();
        } else {
            throw new YuSyntaxError("trying to break loop outside a loop");
        }
        return null;
    }

    @Override
    public Void visitCondition(YuCondition condition, YuContext value) {
        //This will be handled by ConditionalExpression
        return null;
    }

    @Override
    public Void visitConditionalExpression(YuConditionalExpression expr, YuContext value) {
        //This will be handled by while/for/if...
        return null;
    }

    @Override
    public Void visitFunction(YuFunction function, YuContext value) {
        return null;
    }

    @Override
    public Void visitModuleFunctionCall(YuModuleFunctionCall call, YuContext value) {
        Function function = call.resolvedFunction;
        if (function != null) {
            // Fast call
            invokeFunction(function, call, value);
            return null;
        }
        int resolvedId = call.getResolvedModuleId();
        if (resolvedId == -1) {
            resolvedId = functionManager.getModuleId(call.getModuleName());
            call.setResolvedModuleId(resolvedId);
        }
        YuModule module = functionManager.getModule(resolvedId);
        if (module == null) {
            throw new YuSyntaxError("module '" + call.getModuleName() + "' not found");
        }
        function = module.getFunction(call.getFunctionName(), call.getArguments().size());
        if (!invokeFunction(function, call, value)) {
            function = module.getFunction(call.getFunctionName(), -1);
            if (invokeFunction(function, call, value)) {
                call.resolvedFunction = function;
            } else {
                throw new Error("can not find target function '" + call.getFunctionName() + "' in module '" + module.getName() + "'");
            }
        } else {
            call.resolvedFunction = function;
        }
        return null;
    }

    private boolean invokeFunction(Function function, YuFunctionCall call, YuContext value) {
        if (function != null) {
            try {
                function.invoke(call.arguments, value, this);
            } catch (Throwable e) {
                throw new Error("Exception occurred in function(custom) call", e);
            }
            return true;
        }
        return false;
    }
}
