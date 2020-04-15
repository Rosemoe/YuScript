/*
  This Java File is Created By Rose
 */
package com.rose.yuscript.functions;

import java.util.List;

import com.rose.yuscript.YuContext;
import com.rose.yuscript.YuInterpreter;
import com.rose.yuscript.tree.YuExpression;

/**
 * @author Rose
 * Function interface for FunctionManager
 */
public interface Function {

	/**
	 * Get the name of function
	 * @return function's name
	 */
	String getName();
	
	/**
	 * Argument count required.
	 * NOTE:
	 * -1 for unspecified (Any count)
	 * NOTE:
	 * Return value is a part of arguments
	 * How to use arguments to set return value is up to you
	 */
	int getArgumentCount();

	/**
	 * Invoke this function
	 * @param arguments Arguments to this function.Always matches this function's argument count
	 * @param context Current context
	 * @param interpreter Current interpreter
	 * @throws Throwable If any error generated
	 */
	void invoke(List<YuExpression> arguments,YuContext context,YuInterpreter interpreter) throws Throwable;
	
}
