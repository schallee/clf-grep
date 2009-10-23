package net.darkmist.clf;

import java.util.Arrays;
import java.util.Comparator;

public class MergingBatchHandler extends LogBatchHandler.Chained.Abstract
{
	private LogEntry[] current = null;
	private Comparator<LogEntry> comparator;

	public MergingBatchHandler(LogBatchHandler next, Comparator<LogEntry> comparator)
	{
		super(next);
		this.comparator = comparator;
	}

	@Override
	public void handleLogBatch(LogBatch newBatch)
	{
		LogEntry[] sortedNewBatch;
		
		sortedNewBatch = newBatch.getLogsAsArray();
		if(!(newBatch instanceof LogBatch.Sorted
			&& comparator.equals(((LogBatch.Sorted)newBatch).getLogComparator())))
			Arrays.sort(sortedNewBatch);
		if(current == null)
			current = sortedNewBatch;
		else
			current  = Util.merge(current, sortedNewBatch, new LogEntry[current.length + sortedNewBatch.length], comparator);
	}

	public void flush()
	{
		LogEntry[] batch;

		if(current == null)
			return;	// nothing to flush...
		batch = current;
		current = null;
		nextHandler(new LogBatch.SortedArrayBased(comparator, batch));
	}
}
