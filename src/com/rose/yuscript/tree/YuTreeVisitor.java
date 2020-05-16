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
package com.rose.yuscript.tree;

/**
 * @author Rose
 */
public interface YuTreeVisitor <R,T> {
	R visitAssignment(YuAssignment assign,T value);
	
	R visitScope(YuScope scope,T value);
	
	R visitCodeBlock(YuCodeBlock codeBlock,T value);
	
	R visitBreak(YuBreak codeBlock,T value);
	
	R visitCondition(YuCondition condition,T value);
	
	R visitConditionalExpression(YuConditionalExpression expr,T value);
	
	R visitEndcode(YuEndcode endcode,T value);
	
	R visitExpression(YuExpression expr,T value);
	
	R visitForTree(YuForTree tree,T value);
	
	R visitFunctionCall(YuFunctionCall call,T value);
	
	R visitIfTree(YuIfTree tree,T value);
	
	R visitValue(YuValue val,T value);
	
	R visitWhileTree(YuWhileTree tree,T value);

	R visitFunction(YuFunction function, T value);

	R visitModuleFunctionCall(YuModuleFunctionCall functionCall, T value);
}
