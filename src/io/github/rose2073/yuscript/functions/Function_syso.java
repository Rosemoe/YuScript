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
package io.github.rose2073.yuscript.functions;

import io.github.rose2073.yuscript.YuContext;
import io.github.rose2073.yuscript.YuInterpreter;
import io.github.rose2073.yuscript.tree.YuExpression;

import java.util.List;

public class Function_syso implements Function {

    private Function_syso() {

    }

    public final static Function_syso INSTANCE = new Function_syso();

    @Override
    public String getName() {
        return "syso";
    }

    @Override
    public int getArgumentCount() {
        return -1;
    }

    @Override
    public void invoke(List<YuExpression> arguments, YuContext context, YuInterpreter interpreter) throws Throwable {
        Object[] val = new Object[1];
        for (YuExpression argument : arguments) {
            val[0] = argument.getValue(context);
            YuMethod.syso(val);
        }
    }
}
