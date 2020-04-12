# YuScript
一个针对iyu v3（裕语言v3）的脚本运行器。   
本项目只包含其核心功能，即语法支持。其中只有一些样例方法。   
如果需要更多功能请自行添加。   
# 自定义函数   
默认只添加`YuMethod`下的方法，为了使功能更加丰富，我们允许你自定义方法。   
## 使用自动化的JavaFunction   
你可以通过把你的方法用`ScriptMethod`注解来表示它是一个脚本方法。  
用`ScriptMethod`标记的方法，如果有返回值，则会自动设置返回值，不需要手动设置。    
用`ScriptMethod`标注的方法可以在参数声明（并且仅能声明）：   
* `Object` 指示传入下一个参数的实际值
* `YuExpression` 指示传入下一个参数的原始表达式
* `YuContext` 指示传入当前运行环境的上下文
* `Object[]` 指示传入函数所有参数的值
* `YuExpression[]` 指示传入函数所有参数的原始表达式
ScriptMethod的可选参数：
* `rtValueAtBegin` 指示函数的返回值应该在首个位置而不是最后一个位置
然后通过`FunctionManager`的`addFunctionFromClass`或者`addFunctionFromMethod`来注册整个类以ScriptMethod标注的方法或者指定的方法到函数查找路径   
提示：使用JavaFunction(Method,String)可以自定义方法名      
## 使用扩展性强的Function   
你可以通过手动实现`Function`这一接口来实现函数。    
实现时必须特别注意：函数的返回值也是一个参数，在函数定义中占位，计入参数个数。   
由自定义Function实现的函数必须手动处理返回值的设定（使用`YuContext`）。
# 特别声明
本项目只为研究交流学习词法分析和抽象语法树产生使用。   
如果本项目涉嫌侵权，请第一时间发送邮件到我的邮箱来通知我删除项目。
