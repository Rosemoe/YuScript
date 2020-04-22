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
 *
 */
public class YuIfTree implements YuNode {

	private YuConditionalExpression condition;
	
	private YuCodeBlock codeBlock;
	
	private YuCodeBlock fallbackCodeBlock;
	
	@Override
	public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
		return visitor.visitIfTree(this, value);
	}
	
	/**
	 * @param codeBlock the codeBlock to set
	 */
	public void setCodeBlock(YuCodeBlock codeBlock) {
		this.codeBlock = codeBlock;
	}
	
	/**
	 * @return the codeBlock
	 */
	public YuCodeBlock getCodeBlock() {
		return codeBlock;
	}
	
	/**
	 * @param condition the condition to set
	 */
	public void setCondition(YuConditionalExpression condition) {
		this.condition = condition;
	}
	
	/**
	 * @return the condition
	 */
	public YuConditionalExpression getCondition() {
		return condition;
	}
	
	/**
	 * @return the fallbackCodeBlock
	 */
	public YuCodeBlock getFallbackCodeBlock() {
		return fallbackCodeBlock;
	}
	
	/**
	 * @param fallbackCodeBlock the fallbackCodeBlock to set
	 */
	public void setFallbackCodeBlock(YuCodeBlock fallbackCodeBlock) {
		this.fallbackCodeBlock = fallbackCodeBlock;
	}

}
