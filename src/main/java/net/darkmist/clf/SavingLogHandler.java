/*
 *  Copyright (C) 2012 Ed Schaller <schallee@darkmist.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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
