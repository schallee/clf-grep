package net.darkmist.clf;

import java.util.Comparator;

public class SortingBatchCombiner extends BatchCombiner
{
	private Comparator<LogEntry> comparator;

	protected SortingBatchCombiner()
	{
	}

	public SortingBatchCombiner(LogBatchHandler next, Comparator<LogEntry> comparator)
	{
		super(next);
		setComparator(comparator);
	}

	protected void setComparator(Comparator<LogEntry> comparator)
	{
		this.comparator = comparator;
	}

	public Comparator<LogEntry> getComparator()
	{
		return comparator;
	}

	/**
	 * Sorts the combined logs before flushing.
	 * @param logs Array containing the LogEntries from the combined batches.
	 * @return LogBatch containing the entries from logs
	 */
	protected LogBatch array2Batch(LogEntry[] logs)
	{
		return new LogBatch.SortedArrayBased(comparator, logs, true);
	}
}
