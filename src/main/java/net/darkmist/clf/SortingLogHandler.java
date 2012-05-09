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
