package net.darkmist.clf;

import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SortingEntries2Batch implements LogHandler
{
	private static final long serialVersionUID = 1l;
	private static final Class<SortingEntries2Batch> CLASS = SortingEntries2Batch.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);
	private LogBatchHandler batchHandler;
	private Comparator<LogEntry> comparator;
	private LogEntry last = null;
	private boolean isSorted = true;
	private List<LogEntry> batchList = new LinkedList<LogEntry>();

	public SortingEntries2Batch()
	{
	}

	public SortingEntries2Batch(Comparator<LogEntry> comparator, LogBatchHandler batchHandler)
	{
		setBatchHandler(batchHandler);
		setComparator(comparator);
	}

	public void setBatchHandler(LogBatchHandler batchHandler)
	{
		this.batchHandler = batchHandler;
	}

	public LogBatchHandler getBatchHandler()
	{
		return batchHandler;
	}

	public void setComparator(Comparator<LogEntry> comparator)
	{
		this.comparator = comparator;
	}

	public void handleLogEntry(LogEntry entry)
	{
		if(entry == null)
			return;
		entry.internStrings();
		Util.checkedAdd(batchList,entry);
		if(!isSorted)
			return;
		if(last == null)
			last = entry;
		else if(comparator.compare(last, entry) <= 0)
		{	// still sorted
			last = entry;
		}
		else
		{	// not sorted...
			last = null;
			isSorted = false;
		}
	}

	/**
	 * Remove all saved log entries and send them to the next handler.
	 */
	public void flush()
	{
		LogEntry[] array;
		boolean wasSorted = isSorted;
		
		if(batchList.size() == 0)
			return;	// don't do anything if we don't have anything
		array = batchList.toArray(LogEntry.EMPTY_ARRAY);
		// clear out the list now that we have the entries in an array.
		batchList.clear();
		last = null;
		isSorted = true;
		// true here means sort it first
		if(!wasSorted && logger.isDebugEnabled())
			logger.debug("wasSorted=" + wasSorted);
		batchHandler.handleLogBatch(new LogBatch.SortedArrayBased(comparator, array, !wasSorted));
	}
}
