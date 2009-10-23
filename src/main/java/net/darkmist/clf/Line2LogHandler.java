package net.darkmist.clf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Line2LogHandler extends LogHandler.Chained.Abstract implements LineHandler
{
	private static final Class<Line2LogHandler> CLASS = Line2LogHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);

	private LogParser parser = new LogParser();

	public Line2LogHandler(LogHandler logHandler)
	{
		super(logHandler);
	}

	public void handleLogEntry(LogEntry entry)
	{
		nextHandler(entry);
	}

	public void handleLine(String line)
	{
		LogEntry entry;

		try
		{
			if((entry = parser.parse(line))!=null)
				nextHandler(entry);
		}
		catch(LogFormatException e)
		{
			logger.warn("Log format exception parsing: " + line, e);
		}
	}
}
