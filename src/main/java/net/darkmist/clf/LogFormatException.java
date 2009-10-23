package net.darkmist.clf;

import java.io.IOException;

public class LogFormatException extends IOException
{
	private static final long serialVersionUID = 1l;

	public LogFormatException()
	{
		super();
	}

	public LogFormatException(String msg)
	{
		super(msg);
	}

	public LogFormatException(String msg, Throwable cause)
	{
		super(msg);
		initCause(cause);
	}

	public LogFormatException(Throwable cause)
	{
		super();
		initCause(cause);
	}
}

