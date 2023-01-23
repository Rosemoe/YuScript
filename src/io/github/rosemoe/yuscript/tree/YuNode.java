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
package io.github.rosemoe.yuscript.tree;

/**
 * @author Rose
 * Abstract syntax tree node interface for iyu
 */
public interface YuNode {

    /**
     * Call method of visitor to accept this node
     *
     * @param <T>     Parameter type
     * @param <R>     Return type
     * @param visitor The visitor you want to invoke
     * @param value   Argument for this visitor
     * @return Value returned by this visitor
     */
    <T, R> R accept(YuTreeVisitor<R, T> visitor, T value);

}
