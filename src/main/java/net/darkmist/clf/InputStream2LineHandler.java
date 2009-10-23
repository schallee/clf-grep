package net.darkmist.clf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InputStream2LineHandler extends LineHandler.Chained.Abstract implements InputStreamHandler
{
	private static final Class<InputStream2LineHandler> CLASS = InputStream2LineHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);

	public InputStream2LineHandler()
	{
	}

	public InputStream2LineHandler(LineHandler next)
	{
		super(next);
	}

	public void handleLine(String line)
	{
		nextHandler(line);
	}

	public void handleBufferedReader(BufferedReader in) throws IOException
	{
		String line;

		while((line = in.readLine())!=null)
			nextLineHandler.handleLine(line);
	}

	public void handleReader(Reader in) throws IOException
	{
		if(in instanceof BufferedReader)
			handleBufferedReader((BufferedReader)in);
		else
			handleBufferedReader(new BufferedReader(in));
	}

	public void handleInputStream(InputStream in) throws IOException
	{
		handleReader(new InputStreamReader(in));
	}
}
