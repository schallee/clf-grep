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

import java.util.List;
import java.util.LinkedList;

public class BatchCombiner extends LogBatchHandler.Chained.Abstract
{
	private List<LogBatch> batches = new LinkedList<LogBatch>();

	protected BatchCombiner()
	{
	}

	public BatchCombiner(LogBatchHandler next)
	{
		super(next);
	}

	public void handleLogBatch(LogBatch batch)
	{
		batches.add(batch);
	}

	/**
	 * Convert the combined LogEntry array to a LogBatch. This is mostly
	 * to allow subclasses to process the final batch before it's flushed
	 * out to the next handler.
	 * @param logs Array containing the LogEntries from the combined batches.
	 * @return LogBatch containing the entries from logs
	 */
	protected LogBatch array2Batch(LogEntry[] logs)
	{
		return new LogBatch.ArrayBased(logs);
	}

	public void flush()
	{
		int numEntries = 0;
		LogEntry[] batchArray;
		LogEntry[] combined;
		int combinedPos = 0;


		for(LogBatch batch : batches)
			numEntries += batch.getNumLogs();
		combined = new LogEntry[numEntries];
		for(LogBatch batch : batches)
		{
			if(batch.preferArray())
			{
				batchArray = batch.getLogsAsArray();
				System.arraycopy(batchArray, 0, combined, combinedPos, batchArray.length);
				combinedPos += batchArray.length;
			}
			else
			{
				for(LogEntry entry : batch.getLogsAsList())
					combined[combinedPos++] = entry;
			}
		}
		batches.clear();
		nextHandler(array2Batch(combined));
	}
}
