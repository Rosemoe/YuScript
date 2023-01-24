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
package io.github.rosemoe.yuscript.functions;

import io.github.rosemoe.yuscript.YuContext;
import io.github.rosemoe.yuscript.YuInterpreter;
import io.github.rosemoe.yuscript.tree.YuExpression;
import io.github.rosemoe.yuscript.tree.YuValue;

import java.util.List;

/**
 * This is used to speed method calls
 * Reflection can be quite slow
 *
 * @author Rose
 */
public class Function_s implements Function {

    private Function_s() {

    }

    public final static Function_s INSTANCE = new Function_s();

    @Override
    public String getName() {
        return "s";
    }

    @Override
    public int getArgumentCount() {
        return 2;
    }

    @Override
    public void invoke(List<YuExpression> arguments, YuContext context, YuInterpreter interpreter) throws Throwable {
        YuExpression expression = arguments.get(1);
        Long result = YuMethod.s(context, arguments.get(0));
        if (expression.getOperators().size() == 0) {
            YuValue value = expression.getChildren().get(0);
            if (value.getType() == YuValue.TYPE_VAR) {
                context.setVariable(value.variablePrefix, value.variableKey, result);
            }
        }
    }
}
