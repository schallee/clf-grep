package net.darkmist.clf;

import java.util.LinkedList;

public class SavingLogHandler extends LinkedList<LogEntry> implements LogHandler.Chained
{
	private static final long serialVersionUID = 1l;
	private LogHandler nextLogHandler;

	public SavingLogHandler()
	{
	}

	public SavingLogHandler(LogHandler nextLogHandler)
	{
		this.nextLogHandler = nextLogHandler;
	}

	protected void nextHandler(LogEntry entry)
	{
		nextLogHandler.handleLogEntry(entry);
	}

	public void setNextLogHandler(LogHandler next)
	{
		nextLogHandler = next;
	}

	public LogHandler getNextLogHandler()
	{
		return nextLogHandler;
	}

	public void handleLogEntry(LogEntry entry)
	{
		if(entry == null)
			return;
		entry.internStrings();
		add(entry);
	}

	public LogEntry[] toLogEntryArray()
	{
		return toArray(LogEntry.EMPTY_ARRAY);
	}

	/**
	 * Remove all saved log entries and send them to the next handler.
	 * Entries are aquired through {@link #toLogEntryArray()} to facilitate quick clearing
	 * of the array and subclass reordering of entries.
	 */
	public void flush()
	{
		LogEntry[] array = toLogEntryArray();

		clear();
		for(LogEntry entry : array)
			nextHandler(entry);
	}
}
