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
