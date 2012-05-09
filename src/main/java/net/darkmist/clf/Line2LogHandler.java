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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Line2LogHandler extends LogHandler.Chained.Abstract implements LineHandler
{
	private static final Class<Line2LogHandler> CLASS = Line2LogHandler.class;
	@SuppressWarnings("unused")
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);

	private LogParser parser = new LogParser();

	public Line2LogHandler(LogHandler logHandler)
	{
		super(logHandler);
	}

	public void handleLogEntry(LogEntry entry)
	{
		nextHandler(entry);
	}

	public void handleLine(String line)
	{
		LogEntry entry;

		try
		{
			if((entry = parser.parse(line))!=null)
				nextHandler(entry);
		}
		catch(LogFormatException e)
		{
			logger.warn("Log format exception parsing: " + line, e);
		}
	}
}
