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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.rose.yuscript.YuContext;
import com.rose.yuscript.YuTokens;

/**
 * @author Rose
 *
 */
public class YuExpression extends YuValue implements YuNode {

	private List<YuValue> children;
	
	private List<YuTokens> operators;
	
	public YuExpression() {
		children = new ArrayList<>();
		operators = new ArrayList<>();
	}
	
	@Override
	public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
		return visitor.visitExpression(this, value);
	}
	
	public void addChild(YuValue child) {
		if(!children.isEmpty()) {
			throw new IllegalStateException();
		}
		children.add(Objects.requireNonNull(child));
	}
	
	public void addExpression(YuTokens op,YuValue child) {
		children.add(Objects.requireNonNull(child));
		operators.add(Objects.requireNonNull(op));
	}
	
	/**
	 * @return the children
	 */
	public List<YuValue> getChildren() {
		return children;
	}
	
	/**
	 * @return the operators
	 */
	public List<YuTokens> getOperators() {
		return operators;
	}

	@Override
	public Object getValue(YuContext context) {
		if(operators.isEmpty()) {
			return children.get(0).getValue(context);
		}
		StringBuilder sb = new StringBuilder().append(children.get(0).getValue(context));
		for(int i = 0;i < operators.size();i++) {
			if(operators.get(i) != YuTokens.PLUS) {
				throw new YuSyntaxError();
			}
			sb.append(children.get(i + 1).getValue(context));
		}
		return sb.toString();
		
	}
}
