package net.darkmist.clf;

import java.util.Arrays;
import java.util.Comparator;

public class SortingLogHandler extends SavingLogHandler
{
	private static final long serialVersionUID = 1l;
	private Comparator<LogEntry> comparator;

	public SortingLogHandler(Comparator<LogEntry> comparator)
	{
		this.comparator = comparator;
	}

	public SortingLogHandler(Comparator<LogEntry> comparator, LogHandler nextLogHandler)
	{
		super(nextLogHandler);
		this.comparator = comparator;
	}

	/**
	 * Returns saved LogEntries in sorted array.
	 * @return Array of sorted LogEntries
	 */
	@Override
	public LogEntry[] toLogEntryArray()
	{
		LogEntry[] a = super.toLogEntryArray();

		Arrays.sort(a, comparator);
		return a;
	}
}
