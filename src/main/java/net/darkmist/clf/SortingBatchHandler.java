package	net.darkmist.clf;

import java.util.Comparator;
import java.util.Arrays;

public class SortingBatchHandler extends LogBatchHandler.Chained.Abstract
{
	private Comparator<LogEntry> comparator;

	protected SortingBatchHandler()
	{
	}

	public SortingBatchHandler(Comparator<LogEntry> comparator)
	{
		setLogComparator(comparator);
	}

	public SortingBatchHandler(LogBatchHandler next)
	{
		super(next);
		setLogComparator(comparator);
	}

	protected void setLogComparator(Comparator<LogEntry> comparator)
	{
		this.comparator = comparator;
	}

	public Comparator<LogEntry> getLogComparator()
	{
		return comparator;
	}

	public void handleLogBatch(LogBatch batch)
	{
		int size;
		LogEntry[] entries;

		if(batch == null)
			return;	// skip null batches
		size = batch.getNumLogs();
		if(size <= 0)
			return;	// skip empty batches
		if(batch instanceof LogBatch.Sorted && comparator.equals(((LogBatch.Sorted)batch).getLogComparator()))
		{	// pass on already sorted batches
			nextHandler(batch);
			return;
		}
		entries = batch.getLogsAsArray();
		batch = null;	// let batch be gc'd
		Arrays.sort(entries, comparator);
		nextHandler(new LogBatch.SortedArrayBased(comparator, entries));
	}
}
