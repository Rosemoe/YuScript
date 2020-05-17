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

import com.rose.yuscript.YuContext;
import com.rose.yuscript.YuTokens;
import static com.rose.yuscript.YuInterpreter.*;

/**
 * @author Rose
 *
 */
public class YuCondition implements YuNode {

	private YuExpression left;

	private YuTokens operator;

	private YuExpression right;

	@Override
	public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
		return visitor.visitCondition(this, value);
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(YuExpression left) {
		this.left = left;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(YuTokens operator) {
		this.operator = operator;
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(YuExpression right) {
		this.right = right;
	}

	/**
	 * @return the left
	 */
	public YuExpression getLeft() {
		return left;
	}

	/**
	 * @return the operator
	 */
	public YuTokens getOperator() {
		return operator;
	}

	/**
	 * @return the right
	 */
	public YuExpression getRight() {
		return right;
	}

	public static double getDouble(Object value) {
		if(value instanceof Number) {
			return ((Number)value).doubleValue();
		} else {
			String str = stringForm(value);
			return str.contains(".") ? Double.parseDouble(str) : Long.parseLong(str);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public boolean getValue(YuContext context) {
		if (operator != null) {
			try {
				switch (operator) {
				case EQEQ:
					return stringForm(left.getValue(context)).equals(stringForm(right.getValue(context)));
				case NOTEQ:
					return !stringForm(left.getValue(context)).equals(stringForm(right.getValue(context)));
				case LT:
					return getDouble(left.getValue(context)) < getDouble(right.getValue(context));
				case GT:
					return getDouble(left.getValue(context)) > getDouble(right.getValue(context));
				case LTEQ:
					return getDouble(left.getValue(context)) <= getDouble(right.getValue(context));
				case GTEQ:
					return getDouble(left.getValue(context)) >= getDouble(right.getValue(context));
				case STARTS_WITH:
					return stringForm(left.getValue(context)).startsWith(stringForm(right.getValue(context)));
				case CONTAINS:
					return stringForm(left.getValue(context)).contains(stringForm(right.getValue(context)));
				case ENDS_WITH:
					return stringForm(left.getValue(context)).endsWith(stringForm(right.getValue(context)));
				}
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			return stringForm(left.getValue(context)).equals("true");
		}
		return false;
	}

}
