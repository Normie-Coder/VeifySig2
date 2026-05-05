/*
 * Created on 2012/11/14
 * Author     eugene kriek
 */
package com.iveri.plugin.miura.exception;

public class mPressException extends Exception
{

	/**
	 *
	 */
	private static final long serialVersionUID = 8382451429504145925L;

	/**
	 * @param message
	 */
	public mPressException(String message)
	{
		super(message);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public mPressException(String message, Throwable cause)
	{
		super(message, cause);

	}
}
