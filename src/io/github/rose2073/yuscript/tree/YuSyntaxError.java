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
package io.github.rose2073.yuscript.tree;

/**
 * @author Rose
 * Syntax error for iyu
 */
public class YuSyntaxError extends Error {

	private static final long serialVersionUID = -2093795813319607171L;

	public YuSyntaxError() {
	}

	public YuSyntaxError(String message) {
		super(message);
	}

	public YuSyntaxError(Throwable cause) {
		super(cause);
	}

	public YuSyntaxError(String message, Throwable cause) {
		super(message, cause);
	}

}
