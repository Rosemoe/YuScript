/*
  This Java File is Created By Rose
 */
package com.rose.yuscript.tree;

/**
 * @author Rose
 *
 */
public class YuSyntaxError extends Error {


	private static final long serialVersionUID = -2093795813319607171L;


	public YuSyntaxError() {
	}


	public YuSyntaxError(String message) {
		super(message);
	}


	public YuSyntaxError(Throwable cause) {
		super(cause);
	}


	public YuSyntaxError(String message, Throwable cause) {
		super(message, cause);
	}

	public YuSyntaxError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
