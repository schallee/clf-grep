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
