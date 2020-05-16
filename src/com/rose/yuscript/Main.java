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
package com.rose.yuscript;

import com.rose.yuscript.functions.YuModule;
import com.rose.yuscript.tree.YuTokenizer;
import com.rose.yuscript.tree.YuTree;

/**
 * @author Rose
 */
public class Main {

	private final static String SAMPLE_CODE = "s a = 0\n" +
			"syso(a,a,a)\n" +
			"for(1;5){\n" +
			"s(a+1,a)\n" +
			"f(a > 3){\n" +
			"break\n" +
			"}\n" +
			"syso(\"a = \" + a)" +
			"}\n" +
			"cls(\"int[]\",class)\n" +
			"syso(class)\n" +
			"javanew(b,\"java.lang.StringBuilder\")\n" +
			"syso(b)\n" +
			"java(b,b,\"StringBuilder.append\",\"String\",233)\n" +
			"syso(b)\n" +
			"javax(null,b,\"StringBuilder\",\"setLength\",\"int\",1)\n" +
			"syso(b)\n" +
			"nsz(50,\"byte\",arr)" +
			"javass(b,\"AbstractStringBuilder\",\"value\",arr)\n" +
			"syso(b)\n" +
			"javags(v,b,\"AbstractStringBuilder\",\"value\")\n" +
			"syso(v)\n" +
			"print(\"Test\")\n" +
			"fn test.call()\n" +
			"test.call()" +
			"" +
			"fn print(param)\n" +

			"test()" +
			"syso(\"My output is:\" + param)\n" +

			"fn test()\n" +
			"syso(\"Inner function\")" +
			"end fn\n" +

			"end fn\n";

	private final static String SAMPLE_MODULE = "" +
			"fn call()\n" +
			"syso(\"call() method is called\")" +
			"end fn\n";

	public static void main(String[] args){
		YuInterpreter i = new YuInterpreter(0);
		YuModule module = new YuModule("test");
		module.addTree(new YuTree(new YuTokenizer(SAMPLE_MODULE)));
		i.getFunctionManager().putModule(module);
		i.eval(SAMPLE_CODE);
	}

}
