/*
 * This Java File is Created By Rose
 */
package com.rose.yuscript.functions;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

import com.rose.yuscript.YuContext;
import com.rose.yuscript.annotation.ScriptMethod;
import com.rose.yuscript.tree.YuCodeBlock;
import com.rose.yuscript.tree.YuExpression;
import com.rose.yuscript.tree.YuSyntaxError;
import com.rose.yuscript.tree.YuValue;

import static com.rose.yuscript.YuInterpreter.*;

/**
 * @author Rose
 *
 */
@SuppressWarnings("unused")
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
		new Thread(() -> context.getDeclaringInterpreter().visitCodeBlock(block, newContext)).start();
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

	@ScriptMethod
	public static void tw(Object[] objs) {
		syso(objs);
	}

	@ScriptMethod
	public static Long s(YuContext context,YuExpression expr) {
		return calculate(context,expr);
	}

	@ScriptMethod
	public static Double s2(YuContext context,YuExpression expr) {
		BigDecimal bg = BigDecimal.valueOf(calculate2(context, expr));
		return bg.setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@ScriptMethod
	public static Double sn(YuContext context,YuExpression expr) {
		return calculate2(context, expr);
	}

	@SuppressWarnings("incomplete-switch")
	public static Long calculate(YuContext context,YuValue expr) {
		if(expr instanceof YuExpression) {
			YuExpression e = (YuExpression) expr;
			long ans = 0;
			long composing = calculate(context, e.getChildren().get(0));
			boolean plusOrMinus = true;
			for(int i = 0;i < e.getOperators().size();i++) {
				long val = calculate(context, e.getChildren().get(i + 1));
				switch(e.getOperators().get(i)) {
					case PLUS:
						if(plusOrMinus) {
							ans += composing;
						}else {
							ans -= composing;
						}
						composing = val;
						plusOrMinus = true;
						break;
					case MINUS:
						if(plusOrMinus) {
							ans += composing;
						}else {
							ans -= composing;
						}
						composing = val;
						plusOrMinus = false;
						break;
					case MULTIPLY:
						composing *= val;
						break;
					case DIVIDE:
						composing /= val;
						break;
				}
			}
			if(plusOrMinus) {
				ans += composing;
			}else {
				ans -= composing;
			}
			return ans;
		}else {
			Object value = null;
			switch(expr.getType()) {
				case YuValue.TYPE_BOOL:
					value = expr.isInvert() == (!expr.getBool());
					if((Boolean)value) {
						value = 1;
					}else {
						value = 0;
					}
					break;
				case YuValue.TYPE_NULL:
					value = 0;
					break;
				case YuValue.TYPE_NUM:
					value = expr.getNumber();
					break;
				case YuValue.TYPE_STR:
					value = expr.getString();
					break;
				case YuValue.TYPE_VAR:
					value = expr.getValue(context);
			}
			if(value == null) {
				return 0L;
			}else if(value instanceof Number){
				return ((Number)value).longValue();
			}else {
				return (long)Double.parseDouble(value instanceof String ? (String)value : value.toString());
			}
		}
	}
	@SuppressWarnings("incomplete-switch")
	public static Double calculate2(YuContext context, YuValue expr) {
		if(expr instanceof YuExpression) {
			YuExpression e = (YuExpression) expr;
			double ans = 0D;
			double composing = calculate2(context, e.getChildren().get(0));
			boolean plusOrMinus = true;
			for(int i = 0;i < e.getOperators().size();i++) {
				double val = calculate2(context, e.getChildren().get(i + 1));
				switch(e.getOperators().get(i)) {
					case PLUS:
						if(plusOrMinus) {
							ans += composing;
						}else {
							ans -= composing;
						}
						composing = val;
						plusOrMinus = true;
						break;
					case MINUS:
						if(plusOrMinus) {
							ans += composing;
						}else {
							ans -= composing;
						}
						composing = val;
						plusOrMinus = false;
						break;
					case MULTIPLY:
						composing *= val;
						break;
					case DIVIDE:
						composing /= val;
						break;
				}
			}
			if(plusOrMinus) {
				ans += composing;
			}else {
				ans -= composing;
			}
			return ans;
		}else {
			Object value = null;
			switch(expr.getType()) {
				case YuValue.TYPE_BOOL:
					value = expr.isInvert() == (!expr.getBool());
					if((Boolean)value) {
						value = 1;
					}else {
						value = 0;
					}
					break;
				case YuValue.TYPE_NULL:
					value = 0;
					break;
				case YuValue.TYPE_NUM:
					value = expr.getNumber();
					break;
				case YuValue.TYPE_STR:
					value = expr.getString();
					break;
				case YuValue.TYPE_VAR:
					value = expr.getValue(context);
			}
			if(value == null) {
				return 0D;
			}else if(value instanceof Number){
				return ((Number)value).doubleValue();
			}else {
				return Double.parseDouble(value instanceof String ? (String)value : value.toString());
			}
		}
	}

	@ScriptMethod
	public static String ssg(Object str,Object p1,Object p2) {
		return getString(str).substring(getInt(p1), getInt(p2));
	}

	@ScriptMethod
	public static String sj(Object str,Object p1,Object p2) {
		String a = getString(p1);
		String b = getString(p2);
		String c = getString(str);
		return c.substring(c.indexOf(a) + a.length() , c.indexOf(b));
	}

	@ScriptMethod
	public static String sr(Object str,Object p1,Object p2) {
		String a = getString(p1);
		String b = getString(p2);
		String c = getString(str);
		return c.replace(a, b);
	}

	@ScriptMethod
	public static String sr(Object str,Object p1,Object p2,Object action) {
		if(getBool(action)) {
			String a = getString(p1);
			String b = getString(p2);
			String c = getString(str);
			return c.replaceAll(a, b);
		}else {
			return sr(str, p1, p2);
		}
	}

	@ScriptMethod
	public static String[] sl(Object a,Object b) {
		return getString(a).split(getString(b));
	}

	@ScriptMethod
	public static int sgszl(Object array) {
		try {
			return Array.getLength(array);
		}catch (Exception e) {
			return -1;
		}
	}

	@ScriptMethod
	public static Object sgsz(Object array,Object pos) {
		try {
			return Array.get(array,getInt(pos));
		}catch (Exception e) {
			return null;
		}
	}

	@ScriptMethod
	public static void sssz(Object array,Object pos,Object val) {
		if(array.getClass().isArray()) {
			Array.set(array, getInt(val), val);
		}else {
			throw new IllegalArgumentException();
		}
	}

	private static boolean getBool(Object p) {
		if(p == null) {
			return false;
		}else if(p instanceof Boolean) {
			return (Boolean) p;
		}else if(p instanceof CharSequence){
			try {
				return Boolean.parseBoolean(p.toString());
			}catch (Exception e) {
				return true;
			}
		}else {
			return true;
		}
	}

	private static String getString(Object p) {
		if(p == null) {
			return "null";
		}else if(p instanceof String) {
			return (String)p;
		}else {
			return String.valueOf(p);
		}
	}

	private static int getInt(Object p) {
		if(p == null) {
			return 0;
		}else if(p instanceof Number) {
			return ((Number)p).intValue();
		}else {
			return Integer.parseInt(p instanceof String ? (String)p : p.toString());
		}
	}


}
