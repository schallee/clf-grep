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

public class Batch2Entries implements LogBatchHandler
{
	private LogHandler logHandler;
	
	public Batch2Entries()
	{
	}

	public Batch2Entries(LogHandler logHandler)
	{
		setLogHandler(logHandler);
	}

	public void setLogHandler(LogHandler logHandler)
	{
		this.logHandler = logHandler;
	}

	public LogHandler getLogHandler()
	{
		return logHandler;
	}

	public void handleLogBatch(LogBatch batch)
	{
		if(logHandler == null)
			throw new IllegalStateException("LogHandler is null!");

		if(batch.preferArray())
			for(LogEntry entry : batch.getLogsAsArray())
				logHandler.handleLogEntry(entry);
		else
			for(LogEntry entry : batch.getLogsAsList())
				logHandler.handleLogEntry(entry);
	}
}
