package net.darkmist.clf;

import java.util.List;
import java.util.LinkedList;

public class BatchCombiner extends LogBatchHandler.Chained.Abstract
{
	private List<LogBatch> batches = new LinkedList<LogBatch>();

	protected BatchCombiner()
	{
	}

	public BatchCombiner(LogBatchHandler next)
	{
		super(next);
	}

	public void handleLogBatch(LogBatch batch)
	{
		batches.add(batch);
	}

	/**
	 * Convert the combined LogEntry array to a LogBatch. This is mostly
	 * to allow subclasses to process the final batch before it's flushed
	 * out to the next handler.
	 * @param logs Array containing the LogEntries from the combined batches.
	 * @return LogBatch containing the entries from logs
	 */
	protected LogBatch array2Batch(LogEntry[] logs)
	{
		return new LogBatch.ArrayBased(logs);
	}

	public void flush()
	{
		int numEntries = 0;
		LogEntry[] batchArray;
		LogEntry[] combined;
		int combinedPos = 0;


		for(LogBatch batch : batches)
			numEntries += batch.getNumLogs();
		combined = new LogEntry[numEntries];
		for(LogBatch batch : batches)
		{
			if(batch.preferArray())
			{
				batchArray = batch.getLogsAsArray();
				System.arraycopy(batchArray, 0, combined, combinedPos, batchArray.length);
				combinedPos += batchArray.length;
			}
			else
			{
				for(LogEntry entry : batch.getLogsAsList())
					combined[combinedPos++] = entry;
			}
		}
		batches.clear();
		nextHandler(array2Batch(combined));
	}
}
