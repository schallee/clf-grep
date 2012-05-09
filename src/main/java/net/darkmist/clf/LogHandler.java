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

public interface LogHandler
{
	public void handleLogEntry(LogEntry entry);

	public static interface Chained extends LogHandler
	{
		public void setNextLogHandler(LogHandler next);
		public LogHandler getNextLogHandler();

		public static abstract class Abstract implements Chained
		{
			private LogHandler nextLogHandler;

			protected Abstract()
			{
			}

			protected Abstract(LogHandler next)
			{
				setNextLogHandler(next);
			}

			public abstract void handleLogEntry(LogEntry entry);

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
		}
	}

	public static class Utils
	{
		private Utils()
		{
		}

		private static final class Synchronized extends Chained.Abstract
		{
			Synchronized(LogHandler next)
			{
				super(next);
			}

			public synchronized void handleLogEntry(LogEntry entry)
			{
				nextHandler(entry);
			}

			public synchronized void setNextLogHandler(LogHandler next)
			{
				super.setNextLogHandler(next);
			}

			public synchronized LogHandler getNextLogHandler()
			{
				return super.getNextLogHandler();
			}
		}

		public static final LogHandler.Chained synchronizedLogHandler(LogHandler target)
		{
			return new Synchronized(target);
		}
	}
}
