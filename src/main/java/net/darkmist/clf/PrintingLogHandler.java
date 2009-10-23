package net.darkmist.clf;

import java.io.PrintWriter;

public class PrintingLogHandler implements LogHandler
{
	private PrintWriter out;
	private LogParser parser = new LogParser();

	public PrintingLogHandler(PrintWriter out)
	{
		this.out = out;
	}

	public void handleLogEntry(LogEntry entry)
	{
		out.println(parser.format(entry));
	}
}
