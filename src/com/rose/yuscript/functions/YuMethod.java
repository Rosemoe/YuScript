/**
 * This Java File is Created By Rose
 */
package com.rose.yuscript.functions;

import java.text.SimpleDateFormat;

import com.rose.yuscript.YuContext;
import com.rose.yuscript.annotation.ScriptMethod;
import com.rose.yuscript.tree.YuCodeBlock;
import com.rose.yuscript.tree.YuSyntaxError;
import static com.rose.yuscript.YuInterpreter.*;

/**
 * @author Rose
 *
 */
public class YuMethod {

	private final static SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	
	/**
	 * No instance
	 */
	private YuMethod() {}

	@ScriptMethod
	public static void syso(Object[] value) {
		System.out.print('[');
		System.out.print(FORMAT.format(System.currentTimeMillis()));
		System.out.print(']');
		for(Object obj : value)
			System.out.println(obj);
	}
	
	@ScriptMethod
	public static void t(YuContext context) {
		if(!context.hasCodeBlock()) {
			throw new YuSyntaxError("code block required");
		}
		final YuContext newContext = new YuContext(context);
		final YuCodeBlock block = context.getCodeBlock();
		new Thread() {
			public void run() {
				context.getDeclaringInterpreter().visitCodeBlock(block, newContext);
			}
		}.start();
	}
	
	@ScriptMethod
	public static String strim(Object obj) {
		return stringForm(obj).trim();
	}
	
	@ScriptMethod
	public static int slg(Object obj) {
		return stringForm(obj).length();
	}
	
	//Test reversed returning statement
	@ScriptMethod(returnValueAtBegin = true)
	public static int rslg(Object obj) {
		return stringForm(obj).length();
	}
	
}
