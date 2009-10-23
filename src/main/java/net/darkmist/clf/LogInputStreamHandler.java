package net.darkmist.clf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogInputStreamHandler implements InputStreamHandler
{
	private static final Class<LogInputStreamHandler> CLASS = LogInputStreamHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);

	private LogHandler logHandler;
	private LogParser parser = new LogParser();

	public LogInputStreamHandler(LogHandler logHandler)
	{
		this.logHandler = logHandler;
	}

	public void handleBufferedReader(BufferedReader in) throws IOException
	{
		String line;
		LogEntry entry;

		while((line = in.readLine())!=null)
			try
			{
				if((entry = parser.parse(line))!=null)
					logHandler.handleLogEntry(entry);
			}
			catch(LogFormatException e)
			{
				logger.warn("Log format exception parsing: " + line, e);
			}
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
