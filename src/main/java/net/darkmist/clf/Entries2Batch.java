package net.darkmist.clf;

public class Entries2Batch implements LogHandler
{
	private static final long serialVersionUID = 1l;
	private LogBatchHandler batchHandler;
	private LogBatch.Appendable batch = newBatch();

	private static LogBatch.Appendable newBatch()
	{
		return new LogBatch.ListBased();
	}

	public Entries2Batch()
	{
	}

	public Entries2Batch(LogBatchHandler batchHandler)
	{
		setBatchHandler(batchHandler);
	}

	public void setBatchHandler(LogBatchHandler batchHandler)
	{
		this.batchHandler = batchHandler;
	}

	public LogBatchHandler getBatchHandler()
	{
		return batchHandler;
	}

	public void handleLogEntry(LogEntry entry)
	{
		if(entry == null)
			return;
		entry.internStrings();
		batch.appendLog(entry);
	}

	/**
	 * Remove all saved log entries and send them to the next handler.
	 */
	public void flush()
	{
		LogBatch localBatch;
		
		localBatch = batch;
		batch = newBatch();

		batchHandler.handleLogBatch(localBatch);
	}
}
