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
