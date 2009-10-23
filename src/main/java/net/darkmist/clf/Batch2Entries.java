package net.darkmist.clf;

public class Batch2Entries implements LogBatchHandler
{
	private LogHandler logHandler;
	
	public Batch2Entries()
	{
	}

	public Batch2Entries(LogHandler logHandler)
	{
		setLogHandler(logHandler);
	}

	public void setLogHandler(LogHandler logHandler)
	{
		this.logHandler = logHandler;
	}

	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void handleLogBatch(LogBatch batch)
	{
		if(logHandler == null)
			throw new IllegalStateException("LogHandler is null!");

		if(batch.preferArray())
			for(LogEntry entry : batch.getLogsAsArray())
				logHandler.handleLogEntry(entry);
		else
			for(LogEntry entry : batch.getLogsAsList())
				logHandler.handleLogEntry(entry);
	}
}
