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
