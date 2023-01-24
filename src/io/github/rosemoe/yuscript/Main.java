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

import io.github.rosemoe.yuscript.functions.YuModule;
import io.github.rosemoe.yuscript.tree.YuTokenizer;
import io.github.rosemoe.yuscript.tree.YuTree;

/**
 * @author Rose
 */
public class Main {

    private final static String SAMPLE_CODE = "math.fib(25,result)";

    private final static String SAMPLE_MODULE = "" +
            "fn fib(i,*r)\n" +
            "f(i <= 2) {\n" +
            "s r = 1\n" +
            "endcode\n" +
            "}\n" +
            "s(i-1,i)\n" +
            "math.fib(i,j)\n" +
            "s(i-1,i)\n" +
            "math.fib(i,k)\n" +
            "s(j+k,r)" +
            "end fn\n";

    public static void main(String[] args) throws Throwable {
        //Create new interpreter
        YuInterpreter i = new YuInterpreter(0);
        //Create new module 'math'
        YuModule module = new YuModule("math");
        //Add methods from tree
        module.addTree(new YuTree(new YuTokenizer(SAMPLE_MODULE)));
        //Add module to manager
        i.getFunctionManager().addModule(module);
        //Parse syntax tree
        YuTree tree = new YuTree(new YuTokenizer(SAMPLE_CODE));
        long startTime = System.nanoTime();
        //Evaluate the syntax tree
        i.eval(tree);
        System.out.println((System.nanoTime() - startTime) / 1e6 + "ms");
        startTime = System.nanoTime();
        //Evaluate the syntax tree
        i.eval(tree);
        System.out.println((System.nanoTime() - startTime) / 1e6 + "ms");
    }

}
