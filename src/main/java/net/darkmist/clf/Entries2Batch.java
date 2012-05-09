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

public class Entries2Batch implements LogHandler
{
	private static final long serialVersionUID = 1l;
	private LogBatchHandler batchHandler;
	private LogBatch.Appendable batch = newBatch();

	private static LogBatch.Appendable newBatch()
	{
		return new LogBatch.ListBased();
	}

	public Entries2Batch()
	{
	}

	public Entries2Batch(LogBatchHandler batchHandler)
	{
		setBatchHandler(batchHandler);
	}

	public void setBatchHandler(LogBatchHandler batchHandler)
	{
		this.batchHandler = batchHandler;
	}

	public LogBatchHandler getBatchHandler()
	{
		return batchHandler;
	}

	public void handleLogEntry(LogEntry entry)
	{
		if(entry == null)
			return;
		entry.internStrings();
		batch.appendLog(entry);
	}

	/**
	 * Remove all saved log entries and send them to the next handler.
	 */
	public void flush()
	{
		LogBatch localBatch;
		
		localBatch = batch;
		batch = newBatch();

		batchHandler.handleLogBatch(localBatch);
	}
}
