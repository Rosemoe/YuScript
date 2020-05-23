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
package io.github.rose2073.yuscript;

import io.github.rose2073.yuscript.functions.YuModule;
import io.github.rose2073.yuscript.tree.YuTokenizer;
import io.github.rose2073.yuscript.tree.YuTree;

/**
 * @author Rose
 */
public class Main {

	private final static String SAMPLE_CODE = "math.fibonacci(20,result)\n" +
            "syso(result)\n" +
			"s(0-5,a)\n" +
			"math.abs(a,a)\n" +
            "s a = \"  Test   \"\n" +
            "strim(a,b)\n" +
			"syso(b)" ;

	private final static String SAMPLE_MODULE = "" +
			"fn fibonacci(i,*r)\n" +
			"f(i <= 2) {\n" +
            "s r = 1\n" +
            "endcode\n" +
            "}\n" +
            "s(i-1,i)\n" +
            "math.fibonacci(i,j)\n" +
            "s(i-1,i)\n" +
            "math.fibonacci(i,k)\n" +
            "s(j+k,r)" +
			"end fn\n" +
			"" +
			"fn abs(val,*dest)\n" +
			"f(val < 0)\n { \n" +
			"s(0 - val,dest)" +
			"}\nelse\n{" +
			"s dest = val\n" +
			"}\n" +
			"end fn\n";

	public static void main(String[] args){
		//Create new interpreter
		YuInterpreter i = new YuInterpreter(0);
		//Create new module 'math'
		YuModule module = new YuModule("math");
		//Add methods from tree
		module.addTree(new YuTree(new YuTokenizer(SAMPLE_MODULE)));
		//Add module to manager
		i.getFunctionManager().putModule(module);
		//Parse syntax tree
        YuTree tree = new YuTree(new YuTokenizer(SAMPLE_CODE));
        //Evaluate the syntax tree
        i.eval(tree);
        long startTime = System.nanoTime();
        //Tree object can be used without limit
        i.eval(tree);
		System.out.println((System.nanoTime() - startTime) / 1e6 + "ms");
	}

}
