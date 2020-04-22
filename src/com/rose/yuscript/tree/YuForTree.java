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
public class YuForTree implements YuNode {

	private YuValue dest;
	
	private YuValue src;
	
	private YuCodeBlock codeBlock;
	
	@Override
	public <T, R> R accept(YuTreeVisitor<R, T> visitor, T value) {
		return visitor.visitForTree(this, value);
	}
	
	/**
	 * @param dest the dest to set
	 */
	public void setDest(YuValue dest) {
		this.dest = dest;
	}
	
	/**
	 * @param src the src to set
	 */
	public void setSrc(YuValue src) {
		this.src = src;
	}
	
	/**
	 * @return the dest
	 */
	public YuValue getDest() {
		return dest;
	}
	
	/**
	 * @return the src
	 */
	public YuValue getSrc() {
		return src;
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

}
