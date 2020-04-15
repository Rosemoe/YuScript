/*
  This Java File is Created By Rose
 */
package com.rose.yuscript;

import java.io.BufferedReader;
import java.io.FileReader;

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
			"syso(\"来自\" + a)" +
			"}";

	public static void main(String[] args){
		YuInterpreter i = new YuInterpreter(0);
		i.eval(SAMPLE_CODE);
	}

}
