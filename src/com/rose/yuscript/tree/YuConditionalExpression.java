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
public class YuConditionalExpression implements YuNode {

	private List<YuCondition> children;
	
	private List<YuTokens> operators;
	
	@Override
	public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
		return visitor.visitConditionalExpression(this, value);
	}
	
	public YuConditionalExpression() {
		children = new ArrayList<>();
		operators = new ArrayList<>();
	}
	
	public void addChild(YuCondition child) {
		if(!children.isEmpty()) {
			throw new IllegalStateException();
		}
		children.add(Objects.requireNonNull(child));
	}
	
	public void addExpression(YuTokens op,YuCondition child) {
		children.add(Objects.requireNonNull(child));
		operators.add(Objects.requireNonNull(op));
	}
	
	/**
	 * @return the children
	 */
	public List<YuCondition> getChildren() {
		return children;
	}
	
	/**
	 * @return the operators
	 */
	public List<YuTokens> getOperators() {
		return operators;
	}
	
	@SuppressWarnings("incomplete-switch")
	public boolean getValue(YuContext context) {
		boolean condition = getChildren().get(0).getValue(context);
		loop:for(int i = 0;i < operators.size();i++) {
			YuTokens operator = operators.get(i);
			switch(operator) {
			case ANDAND:
				if(condition) {
					condition = getChildren().get(i + 1).getValue(context);
				}else {
					break loop;
				}
				break;
			case OROR:
				if(condition) {
					break loop;
				}else {
					condition = getChildren().get(i + 1).getValue(context);
				}
			}
		}
		return condition;
	}

}
